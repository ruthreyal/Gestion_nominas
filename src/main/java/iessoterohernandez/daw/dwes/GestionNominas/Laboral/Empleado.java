package iessoterohernandez.daw.dwes.GestionNominas.Laboral;

public class Empleado extends Persona {

	private int categoria;
	public int anyos;

	public Empleado(String nombre, String dni, char sexo, int categoria, int anyos) throws DatosNoCorrectosException {
		super(nombre, dni, sexo);
		if (categoria < 1 || categoria > 10) {
			throw new DatosNoCorrectosException("Datos no correctos");

		} else {
			this.categoria = categoria;
		}
		if (anyos < 0) {
			throw new DatosNoCorrectosException("Datos no correctos");

		} else {
			this.anyos = anyos;
		}

	}

	public Empleado(String nombre, String dni, char sexo) throws DatosNoCorrectosException {
		super(nombre, dni, sexo);
		categoria = 1;
		if (categoria < 1 || categoria > 10) {
			throw new DatosNoCorrectosException("Datos no correctos");

		}
		anyos = 0;
		if (anyos < 0) {
			throw new DatosNoCorrectosException("Datos no correctos");

		}
	}

	public void setCategoria(int categoriaNueva) {
		categoria = categoriaNueva;
	}

	public void incrAnyo() {
		anyos += 1;
	}

	public void imprime() {
		super.imprime();
		System.out.println(" sexo: " + super.sexo + ". Categoria " + categoria + " años: " + anyos);
	}

	// Getters
	public String getDni() {
		return dni;
	}

	public String getNombre() {
		return nombre;
	}

	public char getSexo() {
		return super.sexo;
	}

	public int getCategoria() {
		return categoria;
	}

	public int getAnyos() {
		return anyos;
	}

	public void setAnyos(int nuevosAnios) {
		anyos = nuevosAnios;

	}

}
