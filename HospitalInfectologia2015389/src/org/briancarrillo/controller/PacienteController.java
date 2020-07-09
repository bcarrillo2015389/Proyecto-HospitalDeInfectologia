
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;
import org.briancarrillo.bean.Paciente;
import org.briancarrillo.db.Conexion;
import org.briancarrillo.sistema.Principal;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javafx.scene.layout.GridPane;
import org.briancarrillo.report.GenerarReporte;

public class PacienteController implements Initializable {
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion=operaciones.NINGUNO;
    private boolean verificacion=false;
    private char[]arrayDPI;
    
    private DatePicker fecha;
    
    private ObservableList<Paciente>listaPaciente;
    @FXML private TextField txtDPI;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtNombres;
    @FXML private TextField txtEdad;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtOcupacion;
    @FXML private TextField txtSexo;
    
    @FXML private TableView tblPacientes;
    @FXML private GridPane grpFecha;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colDPI;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colFechaNacimiento;
    @FXML private TableColumn colEdad;
    @FXML private TableColumn colDireccion;
    @FXML private TableColumn colOcupacion;
    @FXML private TableColumn colSexo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/briancarrillo/resource/DatePicker.css");
        grpFecha.add(fecha, 0, 0);
        init();
    }
    
    public void cargarDatos(){
        tblPacientes.setItems(getPacientes());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Paciente,Integer>("codigoPaciente"));
        colDPI.setCellValueFactory(new PropertyValueFactory<Paciente,String>("DPI"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Paciente,String>("apellidos"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Paciente,String>("nombres"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<Paciente,Date>("fechaNacimiento"));
        colEdad.setCellValueFactory(new PropertyValueFactory<Paciente,Integer>("edad"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Paciente,String>("direccion"));
        colOcupacion.setCellValueFactory(new PropertyValueFactory<Paciente,String>("ocupacion"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Paciente,String>("sexo"));
    }
    
    public ObservableList<Paciente> getPacientes(){
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
        
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
    
    public void seleccionarElemento(){
        if(tblPacientes.getSelectionModel().getSelectedItem()!=null){
            fecha.selectedDateProperty().set(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getFechaNacimiento());
            txtDPI.setText(String.valueOf(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getDPI()));
            txtApellidos.setText(String.valueOf(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getApellidos()));
            txtNombres.setText(String.valueOf(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getNombres()));
            txtEdad.setText(String.valueOf(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getEdad()));
            txtDireccion.setText(String.valueOf(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getDireccion()));
            txtOcupacion.setText(String.valueOf((((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getOcupacion())));
            txtSexo.setText(String.valueOf((((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getSexo())));
        }else{
            tblPacientes.getSelectionModel().clearSelection();
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
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblPacientes.setDisable(false);
                tblPacientes.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                break;
             
            default:
                if(tblPacientes.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar Paciente", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta==JOptionPane.YES_OPTION){
                        try{
                        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarPaciente(?)}");
                        procedimiento.setInt(1, ((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente());
                        procedimiento.execute();
                        listaPaciente.remove(tblPacientes.getSelectionModel().getSelectedIndex());
                        limpiarControles();
                        tblPacientes.getSelectionModel().select(null);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        limpiarControles();
                        tblPacientes.getSelectionModel().select(null);
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
                tblPacientes.setDisable(true);
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblPacientes.getSelectionModel().select(null);
                
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
                    btnReporte.setDisable(false);
                    tipoDeOperacion=operaciones.NINGUNO;
                    tblPacientes.setDisable(false);
                    cargarDatos();
                    }
                break;
                
        }
    }
    
    public void activarControles(){
        txtDPI.setEditable(true);
        txtApellidos.setEditable(true);
        txtNombres.setEditable(true);
        txtEdad.setEditable(false);
        txtDireccion.setEditable(true);
        txtOcupacion.setEditable(true);
        txtSexo.setEditable(true);
        grpFecha.setDisable(false);
    }
    
    public void desactivarControles(){
        txtDPI.setEditable(false);
        txtApellidos.setEditable(false);
        txtNombres.setEditable(false);
        txtEdad.setEditable(false);
        txtDireccion.setEditable(false);
        txtOcupacion.setEditable(false);
        txtSexo.setEditable(false);
        grpFecha.setDisable(true);
    }
    
    public void limpiarControles(){
        txtDPI.setText("");
        txtApellidos.setText("");
        txtNombres.setText("");
        txtEdad.setText("");
        txtDireccion.setText("");
        txtOcupacion.setText("");
        txtSexo.setText("");
        fecha.selectedDateProperty().set(null);
    }
    
    public void guardar(){
        Paciente registro = new Paciente();
        registro.setDPI(txtDPI.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setNombres(txtNombres.getText());
        registro.setFechaNacimiento(fecha.getSelectedDate());
        registro.setDireccion(txtDireccion.getText());
        registro.setOcupacion(txtOcupacion.getText());
        registro.setSexo(txtSexo.getText());
    
        try{
        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarPaciente(?,?,?,?,?,?,?)}");
        procedimiento.setString(1, registro.getDPI());
        procedimiento.setString(2, registro.getApellidos());
        procedimiento.setString(3, registro.getNombres());
        procedimiento.setDate(4, new java.sql.Date(registro.getFechaNacimiento().getTime()));
        procedimiento.setString(5, registro.getDireccion());
        procedimiento.setString(6, registro.getOcupacion());
        procedimiento.setString(7, registro.getSexo());
        procedimiento.execute();
        listaPaciente.add(registro);
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblPacientes.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tblPacientes.setDisable(true);
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
                btnReporte.setText("Reporte");
                tipoDeOperacion=operaciones.NINGUNO;
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                cargarDatos();
                desactivarControles();
                limpiarControles();
                tblPacientes.setDisable(false);
                }
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarPaciente(?,?,?,?,?,?,?,?)}");
            Paciente registro = ((Paciente)tblPacientes.getSelectionModel().getSelectedItem());
            registro.setDPI(txtDPI.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setNombres(txtNombres.getText());
            registro.setFechaNacimiento(fecha.getSelectedDate());
            registro.setDireccion(txtDireccion.getText());
            registro.setOcupacion(txtOcupacion.getText());
            registro.setSexo(txtSexo.getText());
            procedimiento.setInt(1, registro.getCodigoPaciente());
            procedimiento.setString(2, registro.getDPI());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getNombres());
            procedimiento.setDate(5, new java.sql.Date(registro.getFechaNacimiento().getTime()));
            procedimiento.setString(6, registro.getDireccion());
            procedimiento.setString(7, registro.getOcupacion());
            procedimiento.setString(8, registro.getSexo());
            procedimiento.execute();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void generarReporte(){
        switch(tipoDeOperacion){
            case NINGUNO:
                tblPacientes.getSelectionModel().select(null);
                limpiarControles();
                
                imprimirReporte();
                tipoDeOperacion=operaciones.NINGUNO;
                break;
                
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                tblPacientes.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblPacientes.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoMedico", null);
        parametros.put("logo", this.getClass().getResourceAsStream("/org/briancarrillo/images/Logo2.jpg"));
        parametros.put("logo2", this.getClass().getResourceAsStream("/org/briancarrillo/images/MapaGuatemala.jpg"));
        parametros.put("base1", this.getClass().getResourceAsStream("/org/briancarrillo/images/FondoT.jpg"));
        parametros.put("base2", this.getClass().getResourceAsStream("/org/briancarrillo/images/FondoT2.jpg"));
        parametros.put("logoPie", this.getClass().getResourceAsStream("/org/briancarrillo/images/Pie.jpg"));
        GenerarReporte.mostrarReporte("ReportePacientes.jasper", "Reporte de Pacientes", parametros);
    }
    
    public boolean verificarControles(){
        boolean verificado=false;
        
        if(!txtDPI.getText().equals("") && !txtApellidos.getText().equals("") && !txtNombres.getText().equals("") && fecha.getSelectedDate()!=null
                && !txtDireccion.getText().equals("") && !txtOcupacion.getText().equals("") && !txtSexo.getText().equals("")){
            
            arrayDPI=txtDPI.getText().toCharArray();
         
            for(int i=0; i<arrayDPI.length; i++){
                if(arrayDPI[i]!='0' && arrayDPI[i]!='1' && arrayDPI[i]!= '2' && arrayDPI[i]!='3' && arrayDPI[i]!='4' && arrayDPI[i]!='5'
                        && arrayDPI[i]!='6' && arrayDPI[i]!='7' && arrayDPI[i]!='8' && arrayDPI[i]!='9'){
                    i=arrayDPI.length;
                    verificado=false;
                    
                }else{
                    verificado=true;
                }
            }
            
            if(verificado!=true){
                JOptionPane.showMessageDialog(null, "Solamente puede ingresar números en el campo de DPI.");
            }
            
            for(int i=0; i<getPacientes().size();i++){
                if(getPacientes().get(i).getDPI().equals(txtDPI.getText()) && tipoDeOperacion==operaciones.GUARDAR){
                    verificado=false;
                    JOptionPane.showMessageDialog(null, "No puede ingresar el número de DPI de otro Paciente existente.");
                    i=getPacientes().size();
                }
            }   
            
        }else{
            JOptionPane.showMessageDialog(null, "Algunos de los datos solicitados están vacíos, por favor verifique.");
        }
        
        return verificado;
    }
    
    private void init(){
        
        txtDPI.setOnKeyTyped(new EventHandler<KeyEvent>(){
            
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                if(txtDPI.getText().toCharArray().length >12){
                    event.consume();
                }
            }
    
        });
        
        txtApellidos.setOnKeyTyped(new EventHandler<KeyEvent>(){
            
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(txtApellidos.getText().equals("") && Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                 if(txtApellidos.getText().toCharArray().length >99){
                    event.consume();
                }
                 
                 if(!Character.isAlphabetic(caracter) && !Character.isSpaceChar(caracter)){
                     event.consume();
                 }
            }
            
        });
        
        txtApellidos.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(txtApellidos.getText().equals(" ")){
                    txtApellidos.setText("");
                    JOptionPane.showMessageDialog(null, "No es posible utilizar espacios en blanco al inicio de los datos. Intente de nuevo.");
                }
            }
        });
        
        txtNombres.setOnKeyTyped(new EventHandler<KeyEvent>(){
            
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(txtNombres.getText().equals("") && Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                 if(txtNombres.getText().toCharArray().length >99){
                    event.consume();
                }
                 
                 if(!Character.isAlphabetic(caracter) && !Character.isSpaceChar(caracter)){
                     event.consume();
                 }
            }
            
        });
        
        
        txtNombres.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(txtNombres.getText().equals(" ")){
                    txtNombres.setText("");
                    JOptionPane.showMessageDialog(null, "No es posible utilizar espacios en blanco al inicio de los datos. Intente de nuevo.");
                }
            }
        });
        
        txtDireccion.setOnKeyTyped(new EventHandler<KeyEvent>(){
            
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(txtDireccion.getText().equals("") && Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                if(txtDireccion.getText().toCharArray().length >149){
                    event.consume();
                }
            }
            
        });
        
        txtDireccion.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(txtDireccion.getText().equals(" ")){
                    txtDireccion.setText("");
                    JOptionPane.showMessageDialog(null, "No es posible utilizar espacios en blanco al inicio de los datos. Intente de nuevo.");
                }
            }
        });
        
        txtOcupacion.setOnKeyTyped(new EventHandler<KeyEvent>(){
            
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(txtOcupacion.getText().equals("") && Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                if(txtOcupacion.getText().toCharArray().length >49){
                    event.consume();
                }
                
                if(!Character.isAlphabetic(caracter) && !Character.isSpaceChar(caracter)){
                     event.consume();
                 }
            }
            
        });
        
        txtOcupacion.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(txtOcupacion.getText().equals(" ")){
                    txtOcupacion.setText("");
                    JOptionPane.showMessageDialog(null, "No es posible utilizar espacios en blanco al inicio de los datos. Intente de nuevo.");
                }
            }
        });
        
        txtSexo.setOnKeyTyped(new EventHandler<KeyEvent>(){
            
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(txtSexo.getText().equals("") && Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                if(txtSexo.getText().toCharArray().length >14){
                    event.consume();
                }
                
                if(!Character.isAlphabetic(caracter) && !Character.isSpaceChar(caracter)){
                     event.consume();
                 }
            }
            
        });
        
        txtSexo.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(txtSexo.getText().equals(" ")){
                    txtSexo.setText("");
                    JOptionPane.showMessageDialog(null, "No es posible utilizar espacios en blanco al inicio de los datos. Intente de nuevo.");
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
    
    public boolean verificarContactoUrgencia(){
        boolean verificacion=true;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_VerificarContactoUrgencia}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                if(resultado.getString("numeroContacto") == null){
                    verificacion=false;
                }
            }
            
            if(verificacion==false){
                JOptionPane.showMessageDialog(null, "Existen pacientes a los que no se les ha creado un contacto urgencia, por favor verifique.");
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return verificacion;
    }
    
    public void menuPrincipal(){
        
        if(verificarContactoUrgencia()==true){
        escenarioPrincipal.menuPrincipal();
        }
        
    }
    
    public void ventanaContactoUrgencia(){
        escenarioPrincipal.ventanaContactoUrgencia();
    }
    
}
