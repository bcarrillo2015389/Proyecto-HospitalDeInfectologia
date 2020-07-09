
package org.briancarrillo.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.briancarrillo.bean.Especialidad;
import org.briancarrillo.bean.Horario;
import org.briancarrillo.bean.Medico;
import org.briancarrillo.bean.Medico_Especialidad;
import org.briancarrillo.db.Conexion;
import org.briancarrillo.sistema.Principal;

public class Medico_EspecialidadController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private boolean verificacion=false;
    
    private ObservableList<Medico_Especialidad> listaMedicoEspecialidad;
    private ObservableList<Medico>listaMedico;
    private ObservableList<Especialidad>listaEspecialidad;
    private ObservableList<Horario> listaHorario;
    
    @FXML private TextField txtCodigoMedicoEspecialidad;
    @FXML private ComboBox cmbCodigoMedico;
    @FXML private ComboBox cmbCodigoEspecialidad;
    @FXML private ComboBox cmbCodigoHorario;
    
    @FXML private TableView tblMedicosEspecialidad;  
    @FXML private TableColumn colCodigoMedicoEspecialidad;
    @FXML private TableColumn colCodigoMedico;
    @FXML private TableColumn colCodigoEspecialidad;
    @FXML private TableColumn colCodigoHorario;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
            
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoMedico.setItems(getMedicos());
        cmbCodigoEspecialidad.setItems(getEspecialidades());
        cmbCodigoHorario.setItems(getHorarios());
    }
    
    public void cargarDatos(){
        tblMedicosEspecialidad.setItems(getMedicosEspecialidad());
        colCodigoMedicoEspecialidad.setCellValueFactory(new PropertyValueFactory<Medico_Especialidad,Integer>("codigoMedicoEspecialidad"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<Medico_Especialidad,Integer>("codigoMedico"));
        colCodigoEspecialidad.setCellValueFactory(new PropertyValueFactory<Medico_Especialidad,Integer>("codigoEspecialidad"));
        colCodigoHorario.setCellValueFactory(new PropertyValueFactory<Medico_Especialidad,Integer>("codigoHorario"));
    }
    
    public ObservableList<Medico_Especialidad> getMedicosEspecialidad(){
        ArrayList<Medico_Especialidad>lista = new ArrayList<Medico_Especialidad>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicos_Especialidad}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Medico_Especialidad(resultado.getInt("codigoMedicoEspecialidad"),
                                     resultado.getInt("codigoMedico"),
                                     resultado.getInt("codigoEspecialidad"),
                                     resultado.getInt("codigoHorario")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaMedicoEspecialidad = FXCollections.observableList(lista);
    }
    
    public ObservableList<Medico> getMedicos(){
        ArrayList<Medico>lista= new ArrayList<Medico>();
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicos()}");
                ResultSet resultado = procedimiento.executeQuery(); 
                
                while(resultado.next()){
                    lista.add(new Medico(   resultado.getInt("codigoMedico"),
                                            resultado.getInt("licenciaMedica"),
                                            resultado.getString("nombres"),
                                            resultado.getString("apellidos"),
                                            resultado.getString("horaEntrada"),
                                            resultado.getString("horaSalida"),
                                            resultado.getInt("turnoMaximo"),
                                            resultado.getString("sexo")));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return listaMedico = FXCollections.observableList(lista);
    }
    
    public ObservableList<Especialidad> getEspecialidades(){
        ArrayList<Especialidad>lista= new ArrayList<Especialidad>();
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarEspecialidades()}");
                ResultSet resultado = procedimiento.executeQuery(); 
                
                while(resultado.next()){
                    lista.add(new Especialidad(resultado.getInt("codigoEspecialidad"),
                                            resultado.getString("nombreEspecialidad")));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return listaEspecialidad = FXCollections.observableList(lista);
    }
    
    public ObservableList<Horario> getHorarios(){
        ArrayList<Horario>lista= new ArrayList<Horario>();
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarHorarios()}");
                ResultSet resultado = procedimiento.executeQuery(); 
                
                while(resultado.next()){
                    lista.add(new Horario(resultado.getInt("codigoHorario"),
                                            resultado.getString("horarioInicio"),
                                            resultado.getString("horarioSalida"),
                                            resultado.getBoolean("lunes"),
                                            resultado.getBoolean("martes"),
                                            resultado.getBoolean("miercoles"),
                                            resultado.getBoolean("jueves"),
                                            resultado.getBoolean("viernes")));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return listaHorario = FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblMedicosEspecialidad.getSelectionModel().getSelectedItem()!=null){
        txtCodigoMedicoEspecialidad.setText(String.valueOf(((Medico_Especialidad)tblMedicosEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad()));
        cmbCodigoMedico.getSelectionModel().select(buscarMedico(((Medico_Especialidad)tblMedicosEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedico()));
        cmbCodigoEspecialidad.getSelectionModel().select(buscarEspecialidad(((Medico_Especialidad)tblMedicosEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad()));
        cmbCodigoHorario.getSelectionModel().select(buscarHorario(((Medico_Especialidad)tblMedicosEspecialidad.getSelectionModel().getSelectedItem()).getCodigoHorario()));
        }else{
            tblMedicosEspecialidad.getSelectionModel().clearSelection();
        }
    }
    
    public Medico_Especialidad buscarMedicoEspecialidad(int codigoMedicoEspecialidad){
        Medico_Especialidad resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedico_Especialidad(?)}");
            procedimiento.setInt(1, codigoMedicoEspecialidad);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado=new Medico_Especialidad(registro.getInt("codigoMedicoEspecialidad"),
                                     registro.getInt("codigoMedico"),
                                     registro.getInt("codigoEspecialidad"),
                                     registro.getInt("codigoHorario"));
                                        
            }
        }catch(Exception  e){
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public Medico buscarMedico(int codigoMedico){
        Medico resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedico(?)}");
            procedimiento.setInt(1, codigoMedico);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado=new Medico(registro.getInt("codigoMedico"),
                                     registro.getInt("licenciaMedica"),
                                     registro.getString("nombres"),
                                     registro.getString("apellidos"),
                                     registro.getString("horaEntrada"),
                                     registro.getString("horaSalida"),
                                     registro.getInt("turnoMaximo"),
                                     registro.getString("sexo"));
                                        
            }
        }catch(Exception  e){
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public Especialidad buscarEspecialidad(int codigoEspecialidad){
        Especialidad resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarEspecialidad(?)}");
            procedimiento.setInt(1, codigoEspecialidad);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado=new Especialidad(registro.getInt("codigoEspecialidad"),
                                     registro.getString("nombreEspecialidad"));
                                        
            }
        }catch(Exception  e){
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public Horario buscarHorario(int codigoHorario){
        Horario resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarHorario(?)}");
            procedimiento.setInt(1, codigoHorario);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado=new Horario(registro.getInt("codigoHorario"),
                                     registro.getString("horarioInicio"),
                                     registro.getString("horarioSalida"),
                                     registro.getBoolean("lunes"),
                                     registro.getBoolean("martes"),
                                     registro.getBoolean("miercoles"),
                                     registro.getBoolean("jueves"),
                                     registro.getBoolean("viernes"));
                                        
            }
        }catch(Exception  e){
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                tblMedicosEspecialidad.setDisable(false);
                tblMedicosEspecialidad.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                break;
             
            default:
                if(tblMedicosEspecialidad.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar Medico-Especialidad", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta==JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarMedico_Especialidad(?)}");
                            procedimiento.setInt(1, ((Medico_Especialidad)tblMedicosEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
                            procedimiento.execute();
                            listaMedicoEspecialidad.remove(tblMedicosEspecialidad.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblMedicosEspecialidad.getSelectionModel().select(null);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        limpiarControles();
                        tblMedicosEspecialidad.getSelectionModel().select(null);
                    }
                    
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }
      
                tipoDeOperacion=operaciones.NINGUNO;
                break;
        }
    }
    
     public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                tblMedicosEspecialidad.setDisable(true);
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                tblMedicosEspecialidad.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.GUARDAR;
                break;
                
            case GUARDAR:
                verificacion=verificarControles();
                
                if(verificacion==true){
                guardar();
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                tipoDeOperacion=operaciones.NINGUNO;
                tblMedicosEspecialidad.setDisable(false);
                cargarDatos();
                }
                
                break;
        }
    }
     
     public void generarReporte(){
        switch(tipoDeOperacion){
            case NINGUNO:

                break;
        }
    }
     
     public void desactivarControles(){
        cmbCodigoMedico.setDisable(true);
        cmbCodigoEspecialidad.setDisable(true);
        cmbCodigoHorario.setDisable(true);
    }
    
    public void activarControles(){
        cmbCodigoMedico.setDisable(false);
        cmbCodigoEspecialidad.setDisable(false);
        cmbCodigoHorario.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoMedicoEspecialidad.setText("");
        cmbCodigoMedico.getSelectionModel().select(null);
        cmbCodigoEspecialidad.getSelectionModel().select(null);
        cmbCodigoHorario.getSelectionModel().select(null);
    }
    
    public void guardar(){
        Medico_Especialidad registro = new Medico_Especialidad();
        registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        registro.setCodigoEspecialidad(((Especialidad)cmbCodigoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
        registro.setCodigoHorario(((Horario)cmbCodigoHorario.getSelectionModel().getSelectedItem()).getCodigoHorario());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarMedico_Especialidad(?,?,?)}");
            procedimiento.setInt(1, registro.getCodigoMedico());
            procedimiento.setInt(2, registro.getCodigoEspecialidad());
            procedimiento.setInt(3, registro.getCodigoHorario());
            procedimiento.execute();
            listaMedicoEspecialidad.add(registro);
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    public boolean verificarControles(){
        boolean verificado=false;
        
        if(cmbCodigoMedico.getSelectionModel().getSelectedItem() != null && cmbCodigoEspecialidad.getSelectionModel().getSelectedItem() != null && cmbCodigoHorario.getSelectionModel().getSelectedItem() != null){
            
                    verificado=true;
            
        }else{
            JOptionPane.showMessageDialog(null, "Algunos de los datos solicitados están vacíos, por favor verifique.");
        }
        
        return verificado;
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    
}
