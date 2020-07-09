
package org.briancarrillo.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;
import org.briancarrillo.bean.Medico;
import org.briancarrillo.bean.TelefonoMedico;
import org.briancarrillo.db.Conexion;
import org.briancarrillo.sistema.Principal;

public class TelefonoMedicoController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion=operaciones.NINGUNO;
    private boolean verificacion=false;
    private char[]arrayTelefonoPersonal;
    private char[]arrayTelefonoTrabajo;
    
    private ObservableList<TelefonoMedico>listaTelefonoMedico;
    private ObservableList<Medico>listaMedico;
    
    @FXML private TextField txtCodigoTelefonoMedico;
    @FXML private TextField txtTelefonoPersonal;
    @FXML private TextField txtTelefonoTrabajo;
    @FXML private ComboBox cmbCodigoMedico;
    @FXML private TableView tblTelefonosMedico;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colTelefonoTrabajo;
    @FXML private TableColumn colCodigoMedico;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoMedico.setItems(getMedicos());
        init();
    }
    
    public void cargarDatos(){
        tblTelefonosMedico.setItems(getTelefonosMedico());
        colCodigo.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, Integer>("codigoTelefonoMedico"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, String>("telefonoPersonal"));
        colTelefonoTrabajo.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, String>("telefonoTrabajo"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, Integer>("codigoMedico"));
    }
    
    public ObservableList<TelefonoMedico> getTelefonosMedico(){
        ArrayList<TelefonoMedico> lista = new ArrayList<TelefonoMedico>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarTelefonosMedico}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new TelefonoMedico(resultado.getInt("codigoTelefonoMedico"),
                                             resultado.getString("telefonoPersonal"),
                                             resultado.getString("telefonoTrabajo"),
                                             resultado.getInt("codigoMedico")));
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaTelefonoMedico=FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblTelefonosMedico.getSelectionModel().getSelectedItem()!=null){
        txtCodigoTelefonoMedico.setText(String.valueOf(((TelefonoMedico)tblTelefonosMedico.getSelectionModel().getSelectedItem()).getCodigoTelefonoMedico()));
        txtTelefonoPersonal.setText(String.valueOf(((TelefonoMedico)tblTelefonosMedico.getSelectionModel().getSelectedItem()).getTelefonoPersonal()));
        txtTelefonoTrabajo.setText(String.valueOf(((TelefonoMedico)tblTelefonosMedico.getSelectionModel().getSelectedItem()).getTelefonoTrabajo()));
        cmbCodigoMedico.getSelectionModel().select(buscarMedico(((TelefonoMedico)tblTelefonosMedico.getSelectionModel().getSelectedItem()).getCodigoMedico()));
        }else{
            tblTelefonosMedico.getSelectionModel().clearSelection();
        }
    }
    
    public TelefonoMedico buscarTelefonoMedico(int codigoTelefonoMedico){
        TelefonoMedico resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarTelefonoMedico(?)}");
            procedimiento.setInt(1, codigoTelefonoMedico);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado=new TelefonoMedico(registro.getInt("codigoTelefonoMedico"),
                                     registro.getString("telefonoPersonal"),
                                     registro.getString("telefonoTrabajo"),
                                     registro.getInt("codigoMedico"));        
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
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                tblTelefonosMedico.setDisable(false);
                tblTelefonosMedico.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                break;
                
            default:
                if(tblTelefonosMedico.getSelectionModel().getSelectedItem()!= null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar Telefono Medico", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta==JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarTelefonoMedico(?)}");
                            procedimiento.setInt(1, (((TelefonoMedico)tblTelefonosMedico.getSelectionModel().getSelectedItem()).getCodigoTelefonoMedico()));
                            procedimiento.execute();
                            listaTelefonoMedico.remove(tblTelefonosMedico.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblTelefonosMedico.getSelectionModel().select(null);
                         }catch(Exception e){
                             e.printStackTrace();
                         }
                    }else{
                        limpiarControles();
                        tblTelefonosMedico.getSelectionModel().select(null);
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
                tblTelefonosMedico.setDisable(true);
                limpiarControles();
                activarControles();
                cmbCodigoMedico.setDisable(false);
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                tblTelefonosMedico.getSelectionModel().select(null);
                
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
                    btnEditar.setDisable(false);
                    tipoDeOperacion=operaciones.NINGUNO;
                    tblTelefonosMedico.setDisable(false);
                    cargarDatos();
                }
                break;
        }
    }
    
    
    public ObservableList<Medico> getMedicos(){
        ArrayList<Medico>lista= new ArrayList<Medico>();
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicos}");
                ResultSet resultado = procedimiento.executeQuery(); 
                
                while(resultado.next()){
                    lista.add(new Medico(resultado.getInt("codigoMedico"),
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
    
    
    public void activarControles(){
        txtTelefonoPersonal.setEditable(true);
        txtTelefonoTrabajo.setEditable(true);
    }
    
    public void desactivarControles(){
        txtTelefonoPersonal.setEditable(false);
        txtTelefonoTrabajo.setEditable(false);
        cmbCodigoMedico.setDisable(true);
    }
    
    public void limpiarControles(){
        txtCodigoTelefonoMedico.setText("");
        txtTelefonoPersonal.setText("");
        txtTelefonoTrabajo.setText("");
        cmbCodigoMedico.getSelectionModel().select(null);
    }
    
    public void guardar(){
        TelefonoMedico registro = new TelefonoMedico();
        registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
        registro.setTelefonoTrabajo(txtTelefonoTrabajo.getText());
        Medico medico = (Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem();
        registro.setCodigoMedico(medico.getCodigoMedico());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTelefonoMedico(?,?,?)}");
            procedimiento.setString(1, registro.getTelefonoPersonal());
            procedimiento.setString(2, registro.getTelefonoTrabajo());
            procedimiento.setInt(3, registro.getCodigoMedico());
            procedimiento.execute();
            listaTelefonoMedico.add(registro);
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
     public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblTelefonosMedico.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setDisable(false);
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tblTelefonosMedico.setDisable(true);
                    tipoDeOperacion=operaciones.ACTUALIZAR;
                    
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }
                break;
                
            case ACTUALIZAR:
                verificacion=verificarControles();
                
                if(verificacion==true){
                    actualizar();
                    btnEditar.setText("Editar");
                    btnReporte.setText("");
                    btnReporte.setDisable(true);
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    cargarDatos();
                    limpiarControles();
                    desactivarControles();
                    tipoDeOperacion=operaciones.NINGUNO;
                    tblTelefonosMedico.setDisable(false);
                }
                break;
        }
    
    }
     
     public void actualizar(){
         try{
             PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarTelefonoMedico(?,?,?)}");
             TelefonoMedico registro = ((TelefonoMedico)tblTelefonosMedico.getSelectionModel().getSelectedItem());
             registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
             registro.setTelefonoTrabajo(txtTelefonoTrabajo.getText());
             procedimiento.setInt(1, registro.getCodigoTelefonoMedico());
             procedimiento.setString(2, registro.getTelefonoPersonal());
             procedimiento.setString(3, registro.getTelefonoTrabajo());
             procedimiento.execute();
             
         }catch(Exception e){
             e.printStackTrace();
         }
     }
     
     public void reporte(){
         switch(tipoDeOperacion){
             case NINGUNO:
                 
                 break;
                 
             default:
                desactivarControles();
                limpiarControles();
                tblTelefonosMedico.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("");
                btnReporte.setDisable(true);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblTelefonosMedico.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                 break;
         }
     }
     
     public boolean verificarControles(){
         boolean verificado=false;
         boolean telefono1=false;
         boolean telefono2=false;
         
         if(!txtTelefonoPersonal.getText().equals("") && cmbCodigoMedico.getSelectionModel().getSelectedItem() != null){
            
            arrayTelefonoPersonal=txtTelefonoPersonal.getText().toCharArray();
            
            for(int i=0; i<arrayTelefonoPersonal.length; i++){
                if(arrayTelefonoPersonal[i]!='1' && arrayTelefonoPersonal[i]!= '2' && arrayTelefonoPersonal[i]!='3' && arrayTelefonoPersonal[i]!='4' && arrayTelefonoPersonal[i]!='5'
                        && arrayTelefonoPersonal[i]!='6' && arrayTelefonoPersonal[i]!='7' && arrayTelefonoPersonal[i]!='8' && arrayTelefonoPersonal[i]!='9'){
                    i=arrayTelefonoPersonal.length;
                    verificado=false;
                    telefono1=false;
                    
                }else{
                    verificado=true;
                    telefono1=true;
                }
            }
            
            if(verificado!=true && telefono1!=true){
                JOptionPane.showMessageDialog(null, "Solamente puede ingresar números en el campo de Telefono Personal.");
            }
            
            if(verificado==true && telefono1==true){
                
                if(txtTelefonoPersonal.getText().toCharArray().length !=8){
                         verificado=false;
                        telefono1=false;
                        JOptionPane.showMessageDialog(null, "Es obligatorio que ingrese 8 dígitos en el campo Telefono Personal.");
                } 
            }
            
            if(verificado==true && telefono1==true){
                for(int i=0; i<getTelefonosMedico().size();i++){
                    if(getTelefonosMedico().get(i).getTelefonoPersonal().equals((txtTelefonoPersonal.getText())) && tipoDeOperacion==operaciones.GUARDAR){
                        verificado=false;
                        telefono1=false;
                        JOptionPane.showMessageDialog(null, "No puede ingresar el número de telefono personal de otro médico existente.");
                        i=getTelefonosMedico().size();
                    }
                }
            }
            
            if(!txtTelefonoTrabajo.getText().equals("")){
                arrayTelefonoTrabajo=txtTelefonoTrabajo.getText().toCharArray();
            
                for(int i=0; i<arrayTelefonoTrabajo.length; i++){
                    if(arrayTelefonoTrabajo[i]!='0' && arrayTelefonoTrabajo[i]!='1' && arrayTelefonoTrabajo[i]!= '2' && arrayTelefonoTrabajo[i]!='3' && arrayTelefonoTrabajo[i]!='4' && arrayTelefonoTrabajo[i]!='5'
                            && arrayTelefonoTrabajo[i]!='6' && arrayTelefonoTrabajo[i]!='7' && arrayTelefonoTrabajo[i]!='8' && arrayTelefonoTrabajo[i]!='9'){
                        i=arrayTelefonoTrabajo.length;
                        verificado=false;
                        telefono2=false;
                    
                    }else{
                        if(telefono1==true){
                            verificado=true;
                        }
                        telefono2=true;
                    }
                }
            
                if(verificado!=true && telefono2!=true){
                    JOptionPane.showMessageDialog(null, "Solamente puede ingresar números en el campo de Telefono de Trabajo.");
                }
                
                if(telefono2==true){
                
                    if(txtTelefonoTrabajo.getText().toCharArray().length !=8){
                        verificado=false;
                        telefono2=false;
                        JOptionPane.showMessageDialog(null, "Es obligatorio que ingrese 8 dígitos en el campo Telefono de Trabajo.");
                    } 
                }
                
                if(telefono2==true){
                    for(int i=0; i<getTelefonosMedico().size();i++){
                        if(getTelefonosMedico().get(i).getTelefonoTrabajo().equals((txtTelefonoTrabajo.getText())) && tipoDeOperacion==operaciones.GUARDAR){
                            verificado=false;
                            telefono2=false;
                            JOptionPane.showMessageDialog(null, "No puede ingresar el número de telefono de trabajo de otro médico existente.");
                            i=getTelefonosMedico().size();
                        }
                    }
                }
                 
           }
            
            
            for(int i=0; i<getTelefonosMedico().size();i++){
                    if(getTelefonosMedico().get(i).getCodigoMedico() == ((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico() && tipoDeOperacion==operaciones.GUARDAR){
                        verificado=false;
                        JOptionPane.showMessageDialog(null, "No puede crear más de un Telefono Médico para un mismo Médico.");
                        i=getTelefonosMedico().size();
                    }
            }   
            
        }else{
            JOptionPane.showMessageDialog(null, "Algunos de los datos solicitados están vacíos, por favor verifique.");
        }
        
        return verificado;
     }
     
     private void init(){
        txtTelefonoPersonal.setOnKeyTyped(new EventHandler<KeyEvent>(){
            
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                if(txtTelefonoPersonal.getText().toCharArray().length >7){
                    event.consume();
                }
               
            }
            
        });
        
        txtTelefonoTrabajo.setOnKeyTyped(new EventHandler<KeyEvent>(){
            
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                if(txtTelefonoTrabajo.getText().toCharArray().length >7){
                    event.consume();
                }

            }
            
        });
        
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
}
