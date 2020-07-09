
package org.briancarrillo.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.briancarrillo.bean.Medico_Especialidad;
import org.briancarrillo.bean.Paciente;
import org.briancarrillo.bean.ResponsableTurno;
import org.briancarrillo.bean.Turno;
import org.briancarrillo.db.Conexion;
import org.briancarrillo.sistema.Principal;

public class TurnoController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion=operaciones.NINGUNO;
    private boolean verificacion=false;
    private char[]arrayValorCita;
    
    private DatePicker fechaTurno;
    private DatePicker fechaCita;
    
    private ObservableList<Turno>listaTurno;
    private ObservableList<Medico_Especialidad>listaMedicoEspecialidad;
    private ObservableList<ResponsableTurno>listaResponsableTurno;
    private ObservableList<Paciente>listaPaciente;
    
    @FXML private TextField txtCodigoTurno;
    @FXML private TextField txtValorCita;
    @FXML private TableView tblTurnos;
    @FXML private GridPane grpFechaTurno;
    @FXML private GridPane grpFechaCita;
    @FXML private TableColumn colCodigoTurno;
    @FXML private TableColumn colFechaTurno;
    @FXML private TableColumn colFechaCita;
    @FXML private TableColumn colValorCita;
    @FXML private TableColumn colCodigoMedicoEspecialidad;
    @FXML private TableColumn colCodigoTurnoResponsable;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private ComboBox cmbCodigoMedicoEspecialidad;
    @FXML private ComboBox cmbCodigoTurnoResponsable;
    @FXML private ComboBox cmbCodigoPaciente;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        init();
        fechaTurno = new DatePicker(Locale.ENGLISH);
        fechaTurno.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fechaTurno.getCalendarView().todayButtonTextProperty().set("Today");
        fechaTurno.getCalendarView().setShowWeeks(false);
        fechaTurno.getStylesheets().add("/org/briancarrillo/resource/DatePicker.css");
        grpFechaTurno.add(fechaTurno, 0, 0);
        
        fechaCita = new DatePicker(Locale.ENGLISH);
        fechaCita.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fechaCita.getCalendarView().todayButtonTextProperty().set("Today");
        fechaCita.getCalendarView().setShowWeeks(false);
        fechaCita.getStylesheets().add("/org/briancarrillo/resource/DatePicker.css");
        grpFechaCita.add(fechaCita, 0, 0);
        
        cmbCodigoMedicoEspecialidad.setItems(getMedicosEspecialidad());
        cmbCodigoTurnoResponsable.setItems(getResponsablesTurno());
        cmbCodigoPaciente.setItems(getPacientes());
    }
    
    public void cargarDatos(){
        tblTurnos.setItems(getTurnos());
        colCodigoTurno.setCellValueFactory(new PropertyValueFactory<Turno,Integer>("codigoTurno"));
        colFechaTurno.setCellValueFactory(new PropertyValueFactory<Turno,Date>("fechaTurno"));
        colFechaCita.setCellValueFactory(new PropertyValueFactory<Turno,Date>("fechaCita"));
        colValorCita.setCellValueFactory(new PropertyValueFactory<Turno,Double>("valorCita"));
        colCodigoMedicoEspecialidad.setCellValueFactory(new PropertyValueFactory<Turno,Integer>("codigoMedicoEspecialidad"));
        colCodigoTurnoResponsable.setCellValueFactory(new PropertyValueFactory<Turno,Integer>("codigoTurnoResponsable"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<Turno,Integer>("codigoPaciente"));
    }
    
    public ObservableList<Turno> getTurnos(){
        ArrayList<Turno> lista = new ArrayList<Turno>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarTurnos}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Turno(resultado.getInt("codigoTurno"),
                                        resultado.getDate("fechaTurno"),
                                        resultado.getDate("fechaCita"),
                                        resultado.getDouble("valorCita"),
                                        resultado.getInt("codigoMedicoEspecialidad"),
                                        resultado.getInt("codigoTurnoResponsable"),
                                        resultado.getInt("codigoPaciente")));
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaTurno = FXCollections.observableList(lista);
        
    }
    
    public void seleccionarElemento(){
        if(tblTurnos.getSelectionModel().getSelectedItem()!=null){
            fechaTurno.selectedDateProperty().set(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getFechaTurno());
            fechaCita.selectedDateProperty().set(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getFechaCita());
            txtCodigoTurno.setText(String.valueOf(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoTurno()));
            txtValorCita.setText(String.valueOf(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getValorCita()));
             cmbCodigoMedicoEspecialidad.getSelectionModel().select(buscarMedicoEspecialidad(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad()));
             cmbCodigoTurnoResponsable.getSelectionModel().select(buscarResponsableTurno(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoTurnoResponsable()));
             cmbCodigoPaciente.getSelectionModel().select(buscarPaciente(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
        }else{
            tblTurnos.getSelectionModel().clearSelection();
        }
    }
    
    public Paciente buscarPaciente(int codigoPaciente){
        Paciente resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPaciente(?)}");
            procedimiento.setInt(1, codigoPaciente);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado=new Paciente(registro.getInt("codigoPaciente"),
                                     registro.getString("DPI"),
                                     registro.getString("apellidos"),
                                     registro.getString("nombres"),
                                     registro.getDate("fechaNacimiento"),
                                     registro.getInt("edad"),
                                     registro.getString("direccion"),
                                     registro.getString("ocupacion"),
                                     registro.getString("sexo"));
                                        
            }
        }catch(Exception  e){
            e.printStackTrace();
        }
        
        return resultado;
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
    
    public ResponsableTurno buscarResponsableTurno(int codigoResponsableTurno){
        ResponsableTurno resultado = null;
        try{
           
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarResponsableTurno(?)}");
            procedimiento.setInt(1, codigoResponsableTurno);
            ResultSet registro = procedimiento.executeQuery();
            
            while(registro.next()){
                resultado= new ResponsableTurno(registro.getInt("codigoResponsableTurno"),
                                    registro.getString("nombreResponsable"),
                                    registro.getString("apellidosResponsable"),
                                    registro.getString("telefonoPersonal"),
                                    registro.getInt("codigoArea"),
                                    registro.getInt("codigoCargo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public Turno buscarTurno(int codigoTurno){
        Turno resultado = null;
        try{
           
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarTurno(?)}");
            procedimiento.setInt(1, codigoTurno);
            ResultSet registro = procedimiento.executeQuery();
            
            while(registro.next()){
                resultado= new Turno(registro.getInt("codigoTurno"),
                                    registro.getDate("fechaTurno"),
                                    registro.getDate("fechaCita"),
                                    registro.getDouble("valorCita"),
                                    registro.getInt("codigoMedicoEspecialidad"),
                                    registro.getInt("codigoTurnoResponsable"),
                                    registro.getInt("codigoPaciente"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public ObservableList<Paciente> getPacientes(){
        ArrayList<Paciente>lista= new ArrayList<Paciente>();
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarPacientes}");
                ResultSet resultado = procedimiento.executeQuery(); 
                
                while(resultado.next()){
                    lista.add(new Paciente(resultado.getInt("codigoPaciente"),
                                            resultado.getString("DPI"),
                                            resultado.getString("apellidos"),
                                            resultado.getString("nombres"),
                                            resultado.getDate("fechaNacimiento"),
                                            resultado.getInt("edad"),
                                            resultado.getString("direccion"),
                                            resultado.getString("ocupacion"),
                                            resultado.getString("sexo")));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return listaPaciente = FXCollections.observableList(lista);
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
    
    public ObservableList<ResponsableTurno> getResponsablesTurno(){
        ArrayList<ResponsableTurno> lista = new ArrayList<ResponsableTurno>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarResponsablesTurno}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new ResponsableTurno(resultado.getInt("codigoResponsableTurno"),
                                               resultado.getString("nombreResponsable"),
                                               resultado.getString("apellidosResponsable"),
                                               resultado.getString("telefonoPersonal"),
                                               resultado.getInt("codigoArea"),
                                               resultado.getInt("codigoCargo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaResponsableTurno = FXCollections.observableList(lista);
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                tblTurnos.setDisable(false);
                tblTurnos.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                break;
             
            default:
                if(tblTurnos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar Turno", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta==JOptionPane.YES_OPTION){
                        try{
                        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarTurno(?)}");
                        procedimiento.setInt(1, ((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoTurno());
                        procedimiento.execute();
                        listaTurno.remove(tblTurnos.getSelectionModel().getSelectedIndex());
                        limpiarControles();
                        tblTurnos.getSelectionModel().select(null);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        limpiarControles();
                        tblTurnos.getSelectionModel().select(null);
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
                tblTurnos.setDisable(true);
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                cmbCodigoMedicoEspecialidad.setDisable(false);
                cmbCodigoTurnoResponsable.setDisable(false);
                cmbCodigoPaciente.setDisable(false);
                tblTurnos.getSelectionModel().select(null);
                
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
                    tblTurnos.setDisable(false);
                    cargarDatos();
                    }
                break;
                
        }
    }
    
    public void activarControles(){
        txtValorCita.setEditable(true);
        grpFechaTurno.setDisable(false);
        grpFechaCita.setDisable(false);
    }
    
    public void desactivarControles(){
        txtValorCita.setEditable(false);
        cmbCodigoMedicoEspecialidad.setDisable(true);
        cmbCodigoTurnoResponsable.setDisable(true);
        cmbCodigoPaciente.setDisable(true);
        grpFechaTurno.setDisable(true);
        grpFechaCita.setDisable(true);
    }
    
    public void limpiarControles(){
        txtCodigoTurno.setText("");
        txtValorCita.setText("");
        cmbCodigoMedicoEspecialidad.getSelectionModel().select(null);
        cmbCodigoTurnoResponsable.getSelectionModel().select(null);
        cmbCodigoPaciente.getSelectionModel().select(null);
        fechaTurno.selectedDateProperty().set(null);
        fechaCita.selectedDateProperty().set(null);
    }
    
    public void guardar(){
        Turno registro = new Turno();
        registro.setFechaTurno(fechaTurno.getSelectedDate());
        registro.setFechaCita(fechaCita.getSelectedDate());
        registro.setValorCita(Double.parseDouble(txtValorCita.getText()));
        Medico_Especialidad medicoEspecialidad = (Medico_Especialidad)cmbCodigoMedicoEspecialidad.getSelectionModel().getSelectedItem();
        registro.setCodigoMedicoEspecialidad(medicoEspecialidad.getCodigoMedicoEspecialidad());
        ResponsableTurno turnoResponsable = (ResponsableTurno)cmbCodigoTurnoResponsable.getSelectionModel().getSelectedItem();
        registro.setCodigoTurnoResponsable(turnoResponsable.getCodigoResponsableTurno());
        Paciente paciente = (Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem();
        registro.setCodigoPaciente(paciente.getCodigoPaciente());
        
    
        try{
        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTurno(?,?,?,?,?,?)}");
        procedimiento.setDate(1, new java.sql.Date(registro.getFechaTurno().getTime()));
        procedimiento.setDate(2, new java.sql.Date(registro.getFechaCita().getTime()));
        procedimiento.setDouble(3, registro.getValorCita());
        procedimiento.setInt(4, registro.getCodigoMedicoEspecialidad());
        procedimiento.setInt(5, registro.getCodigoTurnoResponsable());
        procedimiento.setInt(6, registro.getCodigoPaciente());
        procedimiento.execute();
        listaTurno.add(registro);
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblTurnos.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setDisable(false);
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tblTurnos.setDisable(true);
                    tipoDeOperacion=operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento."); 
                }
                break;
            
            case ACTUALIZAR:
                verificacion=verificarControles();
                
                if(verificacion==true){
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("");
                btnReporte.setDisable(true);
                tipoDeOperacion=operaciones.NINGUNO;
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                cargarDatos();
                desactivarControles();
                limpiarControles();
                tblTurnos.setDisable(false);
                }
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarTurno(?,?,?,?)}");
            Turno registro = ((Turno)tblTurnos.getSelectionModel().getSelectedItem());
            registro.setFechaTurno(fechaTurno.getSelectedDate());
            registro.setFechaCita(fechaCita.getSelectedDate());
            registro.setValorCita(Double.parseDouble(txtValorCita.getText()));

            procedimiento.setInt(1, registro.getCodigoTurno());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaTurno().getTime()));
            procedimiento.setDate(3, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setDouble(4, registro.getValorCita());
            procedimiento.execute();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
     public void generarReporte(){
        switch(tipoDeOperacion){
            case NINGUNO:
                break;
                
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                tblTurnos.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("");
                btnReporte.setDisable(true);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblTurnos.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                break;
        }
    }
     
     public boolean verificarControles(){
        boolean verificado=false;
        
        if(fechaTurno.getSelectedDate()!=null && fechaCita.getSelectedDate()!=null && !txtValorCita.getText().equals("")
                && cmbCodigoMedicoEspecialidad.getSelectionModel().getSelectedItem()!=null && cmbCodigoTurnoResponsable.getSelectionModel().getSelectedItem()!=null
                && cmbCodigoPaciente.getSelectionModel().getSelectedItem()!=null){
            
            arrayValorCita=txtValorCita.getText().toCharArray();
         
            for(int i=0; i<arrayValorCita.length; i++){
                if(arrayValorCita[i]!='0' && arrayValorCita[i]!='1' && arrayValorCita[i]!= '2' && arrayValorCita[i]!='3' && arrayValorCita[i]!='4' && arrayValorCita[i]!='5'
                        && arrayValorCita[i]!='6' && arrayValorCita[i]!='7' && arrayValorCita[i]!='8' && arrayValorCita[i]!='9' && arrayValorCita[i]!='.'){
                    i=arrayValorCita.length;
                    verificado=false;
                    
                }else{
                    verificado=true;
                }
            }
            
            if(verificado!=true){
                JOptionPane.showMessageDialog(null, "Solamente puede ingresar números y puntos en el campo de Valor de Cita.");
            }
            
        }else{
            JOptionPane.showMessageDialog(null, "Algunos de los datos solicitados están vacíos, por favor verifique.");
        }
        
        return verificado;
    }
     
    public void init(){
        txtValorCita.setOnKeyTyped(new EventHandler<KeyEvent>(){
            
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                char[]array;
                boolean punto;

                if(Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
               
                if(caracter == '.'){
                    
                    if(txtValorCita.getText().equals("")){
                        event.consume();
                    }
                    
                    array = txtValorCita.getText().toCharArray();
                    for(int i=0; i<array.length; i++){
                        if(array[i] == '.'){
                            i=array.length;
                            event.consume();
                        }
                    }
                }
                
                if(Character.isDigit(caracter)){
                    array = txtValorCita.getText().toCharArray();
                    punto=false;
                    
                    for(int i=0; i<array.length; i++){
                        if(array[i] == '.' ){
                            if(i== array.length-3){
                                i=array.length;
                                event.consume(); 
                            }
                            punto=true;
                        }
                    }
                    
                    if(txtValorCita.getText().toCharArray().length >7 && punto==true){
                        event.consume();
                    }else if(txtValorCita.getText().toCharArray().length >4 && punto==false){
                        event.consume();
                    }
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
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
}
