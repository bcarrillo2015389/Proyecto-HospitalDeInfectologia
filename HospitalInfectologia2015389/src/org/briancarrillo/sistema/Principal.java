/*-- Programador: Brian Carrillo 2015389
  -- Control de versiones
  -- Fecha de creacion 27/05/2019*/

package org.briancarrillo.sistema;

import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.briancarrillo.controller.AreaController;
import org.briancarrillo.controller.CargoController;
import org.briancarrillo.controller.ContactoUrgenciaController;
import org.briancarrillo.controller.EspecialidadController;
import org.briancarrillo.controller.HorarioController;
import org.briancarrillo.controller.MedicoController;
import org.briancarrillo.controller.Medico_EspecialidadController;
import org.briancarrillo.controller.MenuPrincipalController;
import org.briancarrillo.controller.PacienteController;
import org.briancarrillo.controller.ProgramadorController;
import org.briancarrillo.controller.ResponsableTurnoController;
import org.briancarrillo.controller.TelefonoMedicoController;
import org.briancarrillo.controller.TurnoController;

public class Principal extends Application {
    private final String PAQUETE_VISTA = "/org/briancarrillo/view/";
    private Stage escenarioPrincipal;
    private Scene escena;
    
    @Override
    public void start(Stage escenarioPrincipal) {
       this.escenarioPrincipal=escenarioPrincipal;
       escenarioPrincipal.setTitle("Hospital de Infectologia");
       escenarioPrincipal.getIcons().add(new Image("/org/briancarrillo/images/hospital_icon.png"));
       menuPrincipal();
       escenarioPrincipal.show();
    }
    
    public void menuPrincipal(){
        try{
        MenuPrincipalController menuPrincipal = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml", 512, 357);
        menuPrincipal.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaMedicos(){
        try{
        MedicoController medicoController = (MedicoController)cambiarEscena("MedicoView.fxml",779,545);
        medicoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void ventanaProgramador(){
        try{
        ProgramadorController programadorController = (ProgramadorController)cambiarEscena("ProgramadorView.fxml",492,356);
        programadorController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void ventanaPacientes(){
        try{
        PacienteController pacienteController = (PacienteController)cambiarEscena("PacienteView.fxml",830,590);
        pacienteController.setEscenarioPrincipal(this);
        }catch(Exception e){
        e.printStackTrace();
        }
    }
    
    public void ventanaEspecialidades(){
        try{
        EspecialidadController especialidadController = (EspecialidadController)cambiarEscena("EspecialidadView.fxml",600,458);
        especialidadController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaAreas(){
        try{
        AreaController areaController = (AreaController)cambiarEscena("AreaView.fxml",600,451);
        areaController.setEscenarioPrincipal(this);
        }catch(Exception e){
        e.printStackTrace();
        }
    }
    
    public void ventanaTelefonosMedico(){
        try{
        TelefonoMedicoController telefonoMedicoController = (TelefonoMedicoController)cambiarEscena("TelefonoMedicoView.fxml",659,566);
        telefonoMedicoController.setEscenarioPrincipal(this);
        }catch(Exception e){
         e.printStackTrace();
        }
    }
    
    public void ventanaCargos(){
        try{
        CargoController cargoController = (CargoController)cambiarEscena("CargoView.fxml",600,456);
        cargoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaContactoUrgencia(){
        try{
        ContactoUrgenciaController contactoUrgenciaController = (ContactoUrgenciaController)cambiarEscena("ContactoUrgenciaView.fxml",739,608);
        contactoUrgenciaController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaResponablesTurno(){
        try{
        ResponsableTurnoController responsableTurnoController = (ResponsableTurnoController)cambiarEscena("ResponsableTurnoView.fxml",779,545);
        responsableTurnoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaHorarios(){
        try{
            HorarioController horarioController = (HorarioController)cambiarEscena("HorarioView.fxml",779,545);
            horarioController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaMedico_Especialidad(){
        try{
            Medico_EspecialidadController medico_especialidadController = (Medico_EspecialidadController) cambiarEscena("Medico_EspecialidadView.fxml", 611, 545);
            medico_especialidadController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTurnos(){
        try{
            TurnoController turnoController = (TurnoController) cambiarEscena("TurnoView.fxml", 1028, 545);
            turnoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
   
    
    
    
    public Initializable cambiarEscena(String fxml, int ancho, int alto) throws Exception{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA+fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA+fxml));
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo),ancho,alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        resultado = (Initializable)cargadorFXML.getController();
        
        return resultado;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
