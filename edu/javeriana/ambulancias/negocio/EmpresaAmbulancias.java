package co.edu.javeriana.ambulancias.negocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import java.util.Set;

import co.edu.javeriana.ambulancias.inteface.IServicioAmbulancias;
import co.edu.javeriana.ambulancias.presentacion.Utils;


public class EmpresaAmbulancias implements IServicioAmbulancias {
		private String nombre;
		private Map<String,IPS> losIPS;
		private List<Servicio> servicios;
		private Map<Integer,Ambulancia> ambulancias;
		
		public EmpresaAmbulancias(String nombre) {
			this.nombre=nombre;
			this.losIPS=new HashMap<String,IPS>();
			this.servicios=new ArrayList<Servicio>();
			this.ambulancias=new HashMap<Integer,Ambulancia>();
		}
		
		public void agregarIPS(String nombre, String tipoAtencion,  String tipoDireccion, int calle, int carrera, int numero){
				
				losIPS.put(nombre,new IPS(nombre, tipoAtencion, tipoDireccion, calle, carrera, numero));
		}
		public void agregarAmbulancia(int codigo,String placa, String tipoAmbulancia, String medico, String tipoUCI)
		{
			if(tipoAmbulancia.equals("BASICA"))
				ambulancias.put(codigo, new AmbulanciaBasica(codigo,placa,medico));
			else if( tipoAmbulancia.equals("UCI"))
				ambulancias.put(codigo, new AmbulanciaUCI(codigo,placa,medico,tipoUCI));
			else if( tipoAmbulancia.equals("NOMEDICALIZADA"))
				ambulancias.put(codigo, new AmbulanciaNoMedicalizada(codigo,placa,medico));
		}
		
		public boolean registrarPosAmbulancia(int codigo,int calle, int carrera)
		{
			boolean esta=false;
			Set<Integer> setKey= ambulancias.keySet();
			for(Integer key : setKey)
			{
				Ambulancia ambulancia=ambulancias.get(key);
				
				if(ambulancia.getCodigo()==codigo)
				{
					ambulancia.setPosicionCalle(calle);
					ambulancia.setPosicionCarrera(carrera);
					ambulancia.sethora();
					ambulancia.setDirModificada(true);
					esta=true;
					break;
				}
				
			}
			return esta;
			
		}
		public String reporteambul(){
			
			if(!ambulancias.isEmpty())
			{
				String todas="Codigo\tPlaca\tTipoDotacion\tHoraPosicion\tPosicionCalle\tPosicionCarrera\tServicio\n"+Utils.imprimirLinea(187)+"\n";
				Set<Integer> setKey= ambulancias.keySet();
				for(Integer key : setKey)
				{
					Ambulancia ambulancia=ambulancias.get(key);
					todas=todas+ambulancia.getCodigo()+"\t";
					todas=todas+ambulancia.getPlaca()+"\t";
					todas=todas+Utils.hora(ambulancia.getHoraPosicion())+"\t\t";
					todas=todas+ambulancia.getPosicionCalle()+"\t\t";
					todas=todas+ambulancia.getPosicionCarrera()+"\t\t";
					if(ambulancia.getServicioAsignado()!=null)
					{
						todas=todas+ambulancia.getServicioAsignado();
						
					}
					todas+="\n";
					
				}
				return todas;				
			}
			else return "No se encuentran ambulancias.";
			
		}
		
		

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
		public long agregarServicio(String nombre, String tipoServicio, String telefono, String tipoDireccion, int n1,
				int n2, int n3) {
			
			Servicio temp=new Servicio(nombre,tipoServicio,telefono,tipoDireccion,n1,n2,n3);
			this.servicios.add(temp);
			return temp.getCodigo();
			
		}
		
		private boolean hayServicioDe(String servicio)
		{
			for(Servicio temp:servicios)
			{
				if(temp.getEstado().equals(servicio))
					return true;
			}
			return false;
		}
		
		public String reporteServiciosNoAsignadas() {
			if(!servicios.isEmpty() && hayServicioDe("NO_ASIGNADO"))
			{
				String reporte="--ASIGNAR UN SERVICIO A UNA AMBULANCIA Y A UN IPS\n";
				reporte+="--Se muestran los servicios del sistema sin asignar:\n";
				reporte+="Codigo\tHoraSolicitud\tPaciente\tTipoServicio\tTelefono\tDireccion\n";
				reporte+="----------------------------------------------------------------------------------------\n";
				
				Collections.sort( servicios,new ServiciosHoraSolicitud() );
				for(Servicio temp:servicios)
				{
					
					if(temp.getEstado().equals("NO_ASIGNADO"))
						reporte+=temp.toString()+"\n";
				}
				return reporte;
			}
			else
			{
				return "No se han encontrado Servicios.";
			}
		}
		public String reporteServiciosSiAsignadas() {
			
			if(!servicios.isEmpty() && hayServicioDe("ASIGNADO"))
			{
				String reporte="--FINALIZAR UN SERVICIO\n";
				reporte+="--Se muestran los servicios del sistema asignados:\n";
				reporte+="Codigo\tPaciente\tAmbulancia\tIPS\n";
				reporte+="----------------------------------------------------------------------------------------\n";
				for(Servicio temp:servicios)
				{
					
					if(temp.getEstado().equals("ASIGNADO"))
						reporte+=temp.toStringEspecial()+"\n";
				}
				return reporte;
			}
			else
			{
				return "No se han encontrado Servicios para finalizar.";
			}
		}
		public boolean verificarCodigoServicio(Long codigo) {
			return buscarServicio(codigo)==null?false:true;
		}
		public String relacionarServicio(Long codigo) {
			
			Servicio servicio=buscarServicio(codigo);
			if(!servicio.getEstado().equals("NO_ASIGNADO"))
				return "El servicio no esta libre";
			Ambulancia ambulancia=ambulanciaMasCercana(servicio);
			if(ambulancia==null)
				return "No se encontro ambulancia disponible";
			IPS ips=null;
			if(servicio.getTipoSercivio()!=Servicio.TipoServicio.DOMICILIO)
			{
				ips=ipsMasCercana(servicio);
				if(ips==null)
					return "No se encomtro IPS disponible";
				
			}
			
			servicio.relacionar(ambulancia,ips);
			return "Al servicio "+codigo+" le fue asignada la ambulancia "+ambulancia.getCodigo()+" y la IPS "+ips.getNombre();
		}
		private Servicio buscarServicio(Long codigo) {
			for(Servicio servicio:this.servicios)
			{
				if(((Long)servicio.getCodigo()).equals(codigo))
				{
					return servicio;
				}
				
			}
			return null;
		}

		private IPS ipsMasCercana(Servicio servicio) {
			int men=999999,valorT;
			IPS menI = null;
			Set<String> setKey= losIPS.keySet();
			for(String key : setKey)
			{
				IPS o=losIPS.get(key);
				valorT=Utils.calcularDistancia(o.getDireccion(),servicio.getDireccion());
				if(valorT<men )
				{
					menI=o;
					men=valorT;
				}
					
			}
			return menI;
		}
		
		private Ambulancia ambulanciaMasCercana(Servicio servicio) {
			int men=999999,valorT;
			Ambulancia menA = null;
			Set<Integer> setKey= ambulancias.keySet();
			for(Integer key : setKey)
			{
				Ambulancia o=ambulancias.get(key);
				valorT=Utils.calcularDistancia(new Direccion(o.getPosicionCalle(),o.getPosicionCarrera()),servicio.getDireccion());
				if(valorT<men&&o.comprovarTipoServicio(servicio)&&!o.isAsignada()&& o.isDirModificada())
				{
					menA=o;
					men=valorT;
				}
					
			}
			return menA;
		}
		public String reporteServiciosIPSAmbulacia() {
			String reporte="--REPORTE DE SERVICIOS CON IPS Y AMBULANCIAS ASOCIADAS\n\n";
		
			for( Servicio servicio: servicios)
			{
				reporte+=servicio.toString(true)+"\n";
			}
			return reporte;
		}
		
		public boolean finAServicio(long codigo)
		{
			
			if(this.verificarCodigoServicio(codigo))
			{
				Servicio servicio=buscarServicio(codigo);
				if(servicio.getEstado().equals("ASIGNADO"))
				{
					servicio.finalizarServicio();
					return true;
				}
			}
			return false;
		}
		
		public String reportarIPS()
		{
			String reporte="";
			Set<String> setKey= losIPS.keySet();
			for(String key : setKey)
			{
				IPS ips=losIPS.get(key);
				reporte+="--------------------------------------------IPS---------------------------------------------------\n"+
						"Nombre\ttipoAtencion\tdireccion\n---------------------------------------------------------------------------\n"
						+ips.getNombre()+"\t"+ips.getTipoAtencion()+"\t"+ips.getDireccion().toString()+"\n"
						+ips.reporteServicios()+"\n";
			}
			return reporte;
			
		}

		public List<Servicio> getServicios() {
			return servicios;
		}

		public Map<Integer, Ambulancia> getAmbulancias() {
			return ambulancias;
		}
		public String estadisticaAmbulanciasDisponibles()
		{
			Set<Integer> setKey= ambulancias.keySet();
			int basicas=0,uci=0,noEspe = 0;
			for(Integer key : setKey)
			{
				Ambulancia o=ambulancias.get(key);
				if(!o.isAsignada()){
					o.reporteAmbulancias(basicas,uci,noEspe);
				}
			}
			return  "-----Estadisticas ambulancias no asignadas---\n"+
				"Basicas: "+basicas+"\nUCI: "+uci+"\nNo Especialisadas: "+noEspe+"\n";
		}
		
}
