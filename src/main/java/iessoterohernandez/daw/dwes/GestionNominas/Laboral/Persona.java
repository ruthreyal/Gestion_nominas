package iessoterohernandez.daw.dwes.GestionNominas.Laboral;

public class Persona {
	
	public String nombre;
	public String dni;
	public char sexo;
	
	public Persona(String nombre, String dni, char sexo) {
		super();
		this.nombre = nombre;
		this.dni = dni;
		this.sexo = sexo;
	}

	public Persona(String nombre, char sexo) {
		super();
		this.nombre = nombre;
		this.sexo = sexo;
	}
	
	
	public void setDni(String dniNuevo) {
		dni=dniNuevo;
	}
	
	public void imprime () {
		System.out.println("Nombre: "+nombre+". Dni:"+dni);
	}
	

}
