package iessoterohernandez.daw.dwes.GestionNominas.Laboral;

import java.util.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CalculaNominas {
	static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) {
		// PRUEBAS
		/*Scanner sc = new Scanner(System.in);
		int opcion;
		
		
		do {
			
			System.out.println("MENU");
			System.out.println("1- Mostrar información de los empleados");
			System.out.println("2- Mostrar el salario");
			System.out.println("3- Modificar datos de los empleados");
			System.out.println("4- Recalcular y actualizar el sueldo de un empleado");
			System.out.println("5- Recalcular y actualizar los sueldos de los empleados");
			System.out.println("6- Realizar una copia de seguridad de la base de datos en ficheros");
			System.out.println("0- Salir");
			System.out.println("Escoja una opción:");
			opcion = sc.nextInt();
			
			
			switch (opcion) {
			
			case 0:
				System.out.println("Adiossss");
			break;
			
			case 1:
				mostrarEmpleados();	
				break;
			
			case 2:
				System.out.println("Indique el dni del empleado:");
				String dni = sc.next();
				mostrarSalario(dni);
				break;
				
			case 3:
				 System.out.println("Indique el dni del empleado a modificar:");
                 dni = sc.next();
                 modificarEmpleado(dni);
                 break;
			case 4:
				System.out.println("Indique el dni del empleado:");
				dni = sc.next();
				recalcularSueldo(dni);
				break;
			case 5: 
				recalcularSueldosParaTodos();
			case 6:
				realizarCopiaDeSeguridad();
				break;
				default:
					System.out.println("Opcion incorrecta");
					break;
			
			}
			
			
			
			
			
		}while(opcion!=0);*/
		
		
		
		
		
		
		
		
		
		
		
		

		// crear un array empleados a traves del archivo empleados.txt y los imprime en
		// pantalla
		List<Empleado> empleados = leerEmpleados("empleados.txt");

		for (Empleado e : empleados) {
			e.imprime();
		}

		// escribe en el archivo salarias.dat el el dni y el salario de los empleados
		escribirSalarios(empleados);
		try {

			// crea un empleado y lo inserta en la base de datos, tambien calcula su sueldo
			// y lo inserta en la tabla nominas.
			Empleado e = new Empleado("Pepe giron", "35000045I", 'M', 4, 3);
			altaEmpleado(e);
			
			//alta empleados a traves del fichero empleadosNuevos.txt
			altaEmpleado("empleadosNuevos.txt");

		} catch (DatosNoCorrectosException e) {
			System.out.println("Error al leer el archivo: " + e.getMessage());
		}
		
		
		
		
		
		

	}

	// METODOS ESTATICOS

	// 4.3 metodo que recibe los valores de los empleados e imprima sus atributos y
	// el sueldo que gana cada uno.
	private static void escribe(Empleado e, Empleado em) {
		Nomina nomina = new Nomina();
		e.imprime();
		System.out.println("Sueldo: " + nomina.sueldo(e) + " euros");
		System.out.println();
		em.imprime();
		System.out.println("Sueldo: " + nomina.sueldo(em) + " euros");
		System.out.println();
	}

	// 1 lee y crea un array de objetos empleados desde un archivo de texto
	private static List<Empleado> leerEmpleados(String fileName) {
	    List<Empleado> empleados = new ArrayList<>();
	    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] datos = line.split(",");
	            String nombre = datos[0];
	            String dni = datos[1];
	            char sexo = datos[2].charAt(0);

	            if (datos.length == 3) {
	                // Usar el constructor con solo nombre, dni y sexo
	                empleados.add(new Empleado(nombre, dni, sexo));
	            } else if (datos.length == 5) {
	                // Usar el constructor con todos los parámetros
	                int categoria = Integer.parseInt(datos[3]);
	                int anyos = Integer.parseInt(datos[4]);
	                empleados.add(new Empleado(nombre, dni, sexo, categoria, anyos));
	            } else {
	                throw new DatosNoCorrectosException("Número incorrecto de datos en la línea: " + line);
	            }
	        }
	    } catch (IOException | DatosNoCorrectosException e) {
	        System.out.println("Error al leer el archivo: " + e.getMessage());
	    }
	    return empleados;
	}

	// 1 escribe los salarios y dni de los trabajadores en el archivo dat desde el
	// array empleados creado en el metodo leerEmpleados
	public static void escribirSalarios(List<Empleado> empleados) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("salarios.dat"))) {
			Nomina nomina = new Nomina();

			for (Empleado empleado : empleados) {
				double sueldo = nomina.sueldo(empleado);
				bw.write(empleado.dni);
				bw.write(" ");
				bw.write(Double.toString(sueldo));
				bw.newLine();
			}

		} catch (IOException e) {
			System.out.println("Error inesperado: " + e.getMessage());
		}
	}

	// 3 metodo que permita dar de alta empleados en el sistema y que de forma
	// automatica dar de alta empleados y calcular su sueldo e incluirlo en las
	// correspondientes tabla de bbdd
	public static void altaEmpleado(Empleado empleado) {
	    Nomina nomina = new Nomina();
	    double sueldo_final = nomina.sueldo(empleado);

	    String sqlEmpleado = "INSERT INTO empleados (dni, nombre, sexo, categoria, anyos) VALUES (?, ?, ?, ?, ?) "
	                       + "ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), sexo = VALUES(sexo), categoria = VALUES(categoria), anyos = VALUES(anyos)";
	    
	    String sqlNomina = "INSERT INTO nominas (dni, categoria, sueldo_final) "
                + "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE categoria = VALUES(categoria), sueldo_final = VALUES(sueldo_final)";
	    try (Connection conn = DBUtils.getConnection();
	         PreparedStatement pstmtEmpleado = conn.prepareStatement(sqlEmpleado);
	         PreparedStatement pstmtNomina = conn.prepareStatement(sqlNomina)) {

	        // Insertar en empleados
	        pstmtEmpleado.setString(1, empleado.getDni());
	        pstmtEmpleado.setString(2, empleado.getNombre());
	        pstmtEmpleado.setString(3, String.valueOf(empleado.getSexo()));
	        pstmtEmpleado.setInt(4, empleado.getCategoria());
	        pstmtEmpleado.setInt(5, empleado.getAnyos());
	        int numFilasEmpleado = pstmtEmpleado.executeUpdate();
	        System.out.println("Se han insertado " + numFilasEmpleado + " filas en empleados");

	        // Insertar en nominas
	        pstmtNomina.setString(1, empleado.getDni());
	        pstmtNomina.setInt(2, empleado.getCategoria());
	        pstmtNomina.setDouble(3, sueldo_final);
	        int numFilasNomina = pstmtNomina.executeUpdate();
	        System.out.println("Se han insertado " + numFilasNomina + " filas en nominas");

	    } catch (SQLException e) {
	        System.out.println("Ocurrió algún error al conectar u operar con la BD: " + e.getMessage());
	        e.printStackTrace(); // Para más detalles sobre el error
	    }
	}

	// 3.1 sobrecarga del metodo altaEmpleado para que permita el alta de empleados en el sistema de forma individual o por lotes apartir de un fichero empleadosNuevos.txt

	private static void altaEmpleado(String fileName) throws DatosNoCorrectosException {
	    List<Empleado> empleados = leerEmpleados(fileName);
	    for (Empleado e : empleados) {
	        altaEmpleado(e);
	    }
	}
	
	//MENU PUNTO 5
	
	//5.1 mostrar informacion de empleados
	private static void mostrarEmpleados() {
		String SQL = "SELECT * FROM empleados";
		try (Connection conn = DBUtils.getConnection();
	             Statement st = conn.createStatement();
	             ResultSet rs = st.executeQuery(SQL)) {
			while (rs.next()) {
				System.out.println("Dni:"+ rs.getString("dni")+ ", nombre: "+rs.getString("nombre")+", sexo: "+rs.getString("sexo")+", categoria: "+rs.getInt("categoria")+", anyos: "+rs.getInt("anyos"));
			}
			
		}catch (SQLException e) {
			System.out.println("Ocurrió algún error al conectar u operar con la BD: " + e.getMessage());
		}
	}
	
	//5.2 mostrar salarios de los empleados
	private static void mostrarSalario(String dni) {
	    String SQL = "SELECT sueldo_final FROM nominas WHERE dni = ?";
	    try (Connection conn = DBUtils.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(SQL)) {
	        
	        pstmt.setString(1, dni);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                System.out.println("Dni: " + dni + ", salario: " + rs.getDouble("sueldo_final"));
	            } else {
	                System.out.println("No se encontró salario para el DNI: " + dni);
	            }
	        }
	    } catch (SQLException e) {
	        System.out.println("Ocurrió algún error al conectar u operar con la BD: " + e.getMessage());
	    }
	    

	    
	}
    
    //metodo para opcion 3 del menu(submenu para modificar):
    private static void modificarEmpleado(String dni) {
    
    	 int subOpcion =-1;
         do {
             System.out.println("SUBMENU - Modificar datos del empleado");
             System.out.println("1- Modificar nombre");
             System.out.println("2- Modificar género");
             System.out.println("3- Modificar categoría");
             System.out.println("4- Modificar años trabajados");
             System.out.println("0- Volver al menú principal");
             System.out.println("Escoja una opción:");
             if (sc.hasNextInt()) {
                 subOpcion = sc.nextInt();
             } else {
                 System.out.println("Por favor, introduzca un número válido.");
                 sc.next();  // Limpiar el buffer de entrada para evitar un bucle infinito
                 continue;  // Volver a solicitar la opción
             }
             
             switch (subOpcion) {
                 case 0:
                     System.out.println("Volviendo al menú principal...");
                     break;

                 case 1:
                     System.out.println("Indique el nuevo nombre:");
                     String nuevoNombre = sc.next();
                     actualizarNombre(dni, nuevoNombre);
                     break;
                 case 2:
                     System.out.println("Indique el nuevo género (M/F):");
                     char nuevoGenero = sc.next().charAt(0);
                     actualizarGenero(dni, nuevoGenero);
                     break;

                 case 3:
                     System.out.println("Indique la nueva categoría:");
                     int nuevaCategoria = sc.nextInt();
                     actualizarCategoria(dni, nuevaCategoria);
                     recalcularSueldo(dni);
                     break;

                 case 4:
                     System.out.println("Indique los nuevos años trabajados:");
                     int nuevosAnios = sc.nextInt();
                     actualizarAniosTrabajados(dni, nuevosAnios);
                     recalcularSueldo(dni);
                     break;
                 default:
                     System.out.println("Opción no válida. Intente de nuevo.");
                     break;
             }
         } while (subOpcion != 0);
     }
    
    //actualizar nombre
    private static void actualizarNombre(String dni, String nuevoNombre) {
        String SQL = "UPDATE empleados SET nombre = ? WHERE dni = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, nuevoNombre);
            pstmt.setString(2, dni);
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Nombre actualizado correctamente.");
            } else {
                System.out.println("No se encontró el empleado con DNI: " + dni);
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar el nombre: " + e.getMessage());
        }
    }
    
   
    //actualizar genero
    private static void actualizarGenero(String dni, char nuevoGenero) {
        String SQL = "UPDATE empleados SET sexo = ? WHERE dni = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, String.valueOf(nuevoGenero));
            pstmt.setString(2, dni);
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Género actualizado correctamente.");
            } else {
                System.out.println("No se encontró el empleado con DNI: " + dni);
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar el género: " + e.getMessage());
        }
    }
    
    //actualizar categoria
    private static void actualizarCategoria(String dni, int nuevaCategoria) {
        String SQL_Empleado = "UPDATE empleados SET categoria = ? WHERE dni = ?";
        String SQL_Nomina = "UPDATE nominas SET categoria = ?, sueldo_final = ? WHERE dni = ?";
        
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmtEmpleado = conn.prepareStatement(SQL_Empleado);
             PreparedStatement pstmtNomina = conn.prepareStatement(SQL_Nomina)) {
            
            // Actualizar la categoría en la tabla empleados
            pstmtEmpleado.setInt(1, nuevaCategoria);
            pstmtEmpleado.setString(2, dni);
            int filasAfectadasEmpleados = pstmtEmpleado.executeUpdate();
            
            if (filasAfectadasEmpleados > 0) {
                System.out.println("Categoría actualizada correctamente en empleados.");
                
                // Recalcular el sueldo del empleado con la nueva categoría
                Empleado empleado = obtenerEmpleadoPorDni(dni); // Método para obtener el empleado con el dni
                empleado.setCategoria(nuevaCategoria); // Actualizar la categoría en el objeto empleado
                Nomina nomina = new Nomina();
                double nuevoSueldo = nomina.sueldo(empleado); // Recalcular el sueldo

                // Actualizar la categoría y sueldo en la tabla nominas
                pstmtNomina.setInt(1, nuevaCategoria);
                pstmtNomina.setDouble(2, nuevoSueldo);
                pstmtNomina.setString(3, dni);
                int filasAfectadasNominas = pstmtNomina.executeUpdate();
                
                if (filasAfectadasNominas > 0) {
                    System.out.println("Categoría y sueldo actualizados correctamente en nominas.");
                } else {
                    System.out.println("No se encontró la nómina para el empleado con DNI: " + dni);
                }
            } else {
                System.out.println("No se encontró el empleado con DNI: " + dni);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al actualizar la categoría y sueldo: " + e.getMessage());
        }
    }
    //obtener empleado por dni
    private static Empleado obtenerEmpleadoPorDni(String dni) {
        String SQL = "SELECT * FROM empleados WHERE dni = ?";
        Empleado empleado = null;
        
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            
            pstmt.setString(1, dni);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    char sexo = rs.getString("sexo").charAt(0);
                    int categoria = rs.getInt("categoria");
                    int anyos = rs.getInt("anyos");
                    
                    empleado = new Empleado(nombre, dni, sexo, categoria, anyos);
                }
            }
        } catch  (DatosNoCorrectosException | SQLException e){
            System.out.println("Error al obtener el empleado: " + e.getMessage());
        }
        
        return empleado;
    }
    
    //actualizar años trabajados
    private static void actualizarAniosTrabajados(String dni, int nuevosAnios) {
        String SQL_Empleado = "UPDATE empleados SET anyos = ? WHERE dni = ?";
        String SQL_Nomina = "UPDATE nominas SET anyos = ?, sueldo_final = ? WHERE dni = ?";
        
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmtEmpleado = conn.prepareStatement(SQL_Empleado);
             PreparedStatement pstmtNomina = conn.prepareStatement(SQL_Nomina)) {
            
            // Actualizar los años trabajados en la tabla empleados
            pstmtEmpleado.setInt(1, nuevosAnios);
            pstmtEmpleado.setString(2, dni);
            int filasAfectadasEmpleados = pstmtEmpleado.executeUpdate();
            
            if (filasAfectadasEmpleados > 0) {
                System.out.println("Años trabajados actualizados correctamente en empleados.");
                
                // Recalcular el sueldo del empleado con los nuevos años trabajados
                Empleado empleado = obtenerEmpleadoPorDni(dni); // Método para obtener el empleado con el dni
                empleado.setAnyos(nuevosAnios); // Actualizar los años en el objeto empleado
                Nomina nomina = new Nomina();
                double nuevoSueldo = nomina.sueldo(empleado); // Recalcular el sueldo

                // Actualizar los años trabajados y sueldo en la tabla nominas
                pstmtNomina.setInt(1, nuevosAnios);
                pstmtNomina.setDouble(2, nuevoSueldo);
                pstmtNomina.setString(3, dni);
                int filasAfectadasNominas = pstmtNomina.executeUpdate();
                
                if (filasAfectadasNominas > 0) {
                    System.out.println("Años trabajados y sueldo actualizados correctamente en nominas.");
                } else {
                    System.out.println("No se encontró la nómina para el empleado con DNI: " + dni);
                }
            } else {
                System.out.println("No se encontró el empleado con DNI: " + dni);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al actualizar los años trabajados y sueldo: " + e.getMessage());
        }
    }
    
    //recalcular sueldo
    private static void recalcularSueldo(String dni) {
        String SQLSelectEmpleado = "SELECT categoria, anyos FROM empleados WHERE dni = ?";
        String SQLUpdateNomina = "UPDATE nominas SET sueldo_final = ? WHERE dni = ?";
        
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmtSelect = conn.prepareStatement(SQLSelectEmpleado);
             PreparedStatement pstmtUpdate = conn.prepareStatement(SQLUpdateNomina)) {
            
            pstmtSelect.setString(1, dni);
            try (ResultSet rs = pstmtSelect.executeQuery()) {
                if (rs.next()) {
                	String nombre = rs.getNString("nombre");
                	String sexoString = rs.getString("sexo");
                	char sexo = sexoString.charAt(0);
                    Empleado empleado = new Empleado(nombre, dni, sexo);
                    Nomina nomina = new Nomina();
                    double nuevoSueldo = nomina.sueldo(empleado);
                    
                    pstmtUpdate.setDouble(1, nuevoSueldo);
                    pstmtUpdate.setString(2, dni);
                    int filasAfectadas = pstmtUpdate.executeUpdate();
                    if (filasAfectadas > 0) {
                        System.out.println("Sueldo recalculado y actualizado correctamente.");
                    } else {
                        System.out.println("No se encontró el empleado con DNI: " + dni);
                    }
                } else {
                    System.out.println("No se encontró el empleado con DNI: " + dni);
                }
            }
        } catch (DatosNoCorrectosException | SQLException e) {
            System.out.println("Error al recalcular el sueldo: " + e.getMessage());
        }
    }
    
    //recalcular sueldo de todos los empleados
    private static void recalcularSueldosParaTodos() {
        String SQLSelectEmpleados = "SELECT dni, nombre, sexo, categoria, anyos FROM empleados";
        String SQLUpdateNomina = "UPDATE nominas SET sueldo_final = ? WHERE dni = ?";
        
        try (Connection conn = DBUtils.getConnection();
             Statement stmt = conn.createStatement();
             PreparedStatement pstmtUpdate = conn.prepareStatement(SQLUpdateNomina);
             ResultSet rs = stmt.executeQuery(SQLSelectEmpleados)) {
            
            while (rs.next()) {
                // Obtener los datos del empleado desde la base de datos
                String dni = rs.getString("dni");
                String nombre = rs.getString("nombre");
                String sexoString = rs.getString("sexo");
                char sexo = sexoString.charAt(0);
                int categoria = rs.getInt("categoria");
                int anyos = rs.getInt("anyos");

                // Crear el objeto Empleado con los datos obtenidos
                Empleado empleado = new Empleado(nombre, dni, sexo, categoria, anyos);

                // Recalcular el sueldo usando el objeto Nomina
                Nomina nomina = new Nomina();
                double nuevoSueldo = nomina.sueldo(empleado);

                // Actualizar el sueldo en la tabla nominas
                pstmtUpdate.setDouble(1, nuevoSueldo);
                pstmtUpdate.setString(2, dni);
                int filasAfectadas = pstmtUpdate.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("Sueldo recalculado y actualizado para el empleado con DNI: " + dni);
                } else {
                    System.out.println("No se encontró el registro en nominas para el empleado con DNI: " + dni);
                }
            }
            
        } catch  (DatosNoCorrectosException | SQLException e){
            System.out.println("Error al recalcular los sueldos: " + e.getMessage());
        }
    }
    
    //copia de seguridad bbdd
    private static void realizarCopiaDeSeguridad() {
        // Rutas de los archivos donde se almacenarán las copias de seguridad
        String archivoEmpleados = "copia_empleados.csv";
        String archivoNominas = "copia_nominas.csv";
        String archivoCategorias = "copia_categorias.csv";
        
        // SQL para extraer los datos de las tablas
        String SQLSelectEmpleados = "SELECT dni, nombre, sexo, categoria, anyos FROM empleados";
        String SQLSelectNominas = "SELECT dni, categoria, sueldo_final FROM nominas";
        String SQLSelectCategorias = "SELECT categoria, sueldo FROM categoria";
        
        try (Connection conn = DBUtils.getConnection();
             Statement stmtEmpleados = conn.createStatement();
             Statement stmtNominas = conn.createStatement();
             Statement stmtCategorias = conn.createStatement();
             ResultSet rsEmpleados = stmtEmpleados.executeQuery(SQLSelectEmpleados);
             ResultSet rsNominas = stmtNominas.executeQuery(SQLSelectNominas);
             ResultSet rsCategorias = stmtCategorias.executeQuery(SQLSelectCategorias)) {
            
            // Escribir los datos de la tabla empleados en un archivo CSV
            try (BufferedWriter bwEmpleados = new BufferedWriter(new FileWriter(archivoEmpleados))) {
                bwEmpleados.write("DNI,Nombre,Sexo,Categoria,Años\n");
                while (rsEmpleados.next()) {
                    bwEmpleados.write(rsEmpleados.getString("dni") + "," +
                                      rsEmpleados.getString("nombre") + "," +
                                      rsEmpleados.getString("sexo") + "," +
                                      rsEmpleados.getInt("categoria") + "," +
                                      rsEmpleados.getInt("anyos") + "\n");
                }
            }
            
            // Escribir los datos de la tabla nominas en un archivo CSV
            try (BufferedWriter bwNominas = new BufferedWriter(new FileWriter(archivoNominas))) {
                bwNominas.write("DNI,Categoria,Sueldo_Final\n");
                while (rsNominas.next()) {
                    bwNominas.write(rsNominas.getString("dni") + "," +
                                    rsNominas.getInt("categoria") + "," +
                                    rsNominas.getDouble("sueldo_final") + "\n");
                }
            }
            
            // Escribir los datos de la tabla categoria en un archivo CSV
            try (BufferedWriter bwCategorias = new BufferedWriter(new FileWriter(archivoCategorias))) {
                bwCategorias.write("Categoria,Sueldo\n");
                while (rsCategorias.next()) {
                    bwCategorias.write(rsCategorias.getInt("categoria") + "," +
                                       rsCategorias.getDouble("sueldo") + "\n");
                }
            }
            
            System.out.println("Copia de seguridad realizada correctamente.");
            
        } catch (SQLException | IOException e) {
            System.out.println("Error al realizar la copia de seguridad: " + e.getMessage());
        }
    }
    
    
    }
	

