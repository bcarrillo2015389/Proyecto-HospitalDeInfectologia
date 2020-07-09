
package org.briancarrillo.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javax.swing.JOptionPane;
import org.briancarrillo.report.GenerarReporte;
import org.briancarrillo.sistema.Principal;
    
public class MenuPrincipalController implements Initializable {
    
    private Principal escenarioPrincipal;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaMedicos(){
        escenarioPrincipal.ventanaMedicos();
    }
    
    public void ventanaProgramador(){
        escenarioPrincipal.ventanaProgramador();
    }
    
    public void ventanaPacientes(){
        escenarioPrincipal.ventanaPacientes();
    }
    
    public void ventanaEspecialidades(){
        escenarioPrincipal.ventanaEspecialidades();
    }
    
    public void ventanaAreas(){
        escenarioPrincipal.ventanaAreas();
    }
    
    public void ventanaTelefonosMedico(){
        escenarioPrincipal.ventanaTelefonosMedico();
    }
    
    public void ventanaCargos(){
        escenarioPrincipal.ventanaCargos();
    }
    
    public void ventanaContactoUrgencia(){
        escenarioPrincipal.ventanaContactoUrgencia();
    }
    
    public void ventanaResponsablesTurno(){
        escenarioPrincipal.ventanaResponablesTurno();
    }
    
    public void ventanaHorarios(){
        escenarioPrincipal.ventanaHorarios();
    }
    
    public void ventanaMedico_Especialidad(){
        escenarioPrincipal.ventanaMedico_Especialidad();
    }
    
    public void ventanaTurnos(){
        escenarioPrincipal.ventanaTurnos();
    }
    
    
    public void imprimirReporteFinal(){
        String cod;
        int codigo;
        Map parametros = new HashMap();
        boolean error=false;
        
        do{
        error=false;
        cod=JOptionPane.showInputDialog("Ingrese el codigo de Medico de quien desea el reporte.");
        
        if(cod!=null){
            try{
                codigo=Integer.parseInt(cod);
                parametros.put("codMedico", codigo);
                parametros.put("logo", this.getClass().getResourceAsStream("/org/briancarrillo/images/Logo2.jpg"));
                parametros.put("logo2", this.getClass().getResourceAsStream("/org/briancarrillo/images/MapaGuatemala.jpg"));
                parametros.put("base1", this.getClass().getResourceAsStream("/org/briancarrillo/images/FondoS.jpg"));
                parametros.put("base2", this.getClass().getResourceAsStream("/org/briancarrillo/images/FondoS2.jpg"));
                parametros.put("logoPie", this.getClass().getResourceAsStream("/org/briancarrillo/images/Pie.jpg"));
                GenerarReporte.mostrarReporte("ReporteFinal.jasper", "Reporte Final", parametros);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Solamente puede ingresar numeros. Por favor intente de nuevo.");
                error=true;
            }
        }
        }while(error==true);
    }
    
    public void imprimirReporteMedicos(){
        Map parametros = new HashMap();
        parametros.put("codigoMedico", null);
        parametros.put("logo", this.getClass().getResourceAsStream("/org/briancarrillo/images/Logo2.jpg"));
        parametros.put("logo2", this.getClass().getResourceAsStream("/org/briancarrillo/images/MapaGuatemala.jpg"));
        parametros.put("base1", this.getClass().getResourceAsStream("/org/briancarrillo/images/FondoT.jpg"));
        parametros.put("base2", this.getClass().getResourceAsStream("/org/briancarrillo/images/FondoT2.jpg"));
        parametros.put("logoPie", this.getClass().getResourceAsStream("/org/briancarrillo/images/Pie.jpg"));
        GenerarReporte.mostrarReporte("ReporteMedicos.jasper", "Reporte de Medicos", parametros);
    }
    
    public void imprimirReportePacientes(){
        Map parametros = new HashMap();
        parametros.put("codigoMedico", null);
        parametros.put("logo", this.getClass().getResourceAsStream("/org/briancarrillo/images/Logo2.jpg"));
        parametros.put("logo2", this.getClass().getResourceAsStream("/org/briancarrillo/images/MapaGuatemala.jpg"));
        parametros.put("base1", this.getClass().getResourceAsStream("/org/briancarrillo/images/FondoT.jpg"));
        parametros.put("base2", this.getClass().getResourceAsStream("/org/briancarrillo/images/FondoT2.jpg"));
        parametros.put("logoPie", this.getClass().getResourceAsStream("/org/briancarrillo/images/Pie.jpg"));
        GenerarReporte.mostrarReporte("ReportePacientes.jasper", "Reporte de Pacientes", parametros);
    }
    
}
