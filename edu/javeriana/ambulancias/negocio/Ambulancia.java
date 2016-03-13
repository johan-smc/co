package co.edu.javeriana.ambulancias.negocio;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class Ambulancia {
	private int codigo;
	private String placa;
	private String tipoDotacion;
	private GregorianCalendar horaPosicion;
	private int posicionCalle;
	private int posicionCarrera;
	private List<Servicio> servicios;
	
	public Ambulancia(int codigo, String placa, String tipoDotacion) {
		this.codigo=codigo;
		this.placa=placa;
		this.tipoDotacion=tipoDotacion;
		this.servicios=new ArrayList();
	}
	public int getCodigo() {
		return codigo;
	}
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	public String getPlaca() {
		return placa;
	}
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	public String getTipoDotacion() {
		return tipoDotacion;
	}
	public void setTipoDotacion(String tipoDotacion) {
		this.tipoDotacion = tipoDotacion;
	}
	public int getPosicionCalle() {
		return posicionCalle;
	}
	public void setPosicionCalle(int posicionCalle) {
		this.posicionCalle = posicionCalle;
	}
	public int getPosicionCarrera() {
		return posicionCarrera;
	}
	public void setPosicionCarrera(int posicionCarrera) {
		this.posicionCarrera = posicionCarrera;
	}
}
