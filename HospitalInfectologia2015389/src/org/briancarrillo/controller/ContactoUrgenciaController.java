
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
import org.briancarrillo.bean.ContactoUrgencia;
import org.briancarrillo.bean.Paciente;
import org.briancarrillo.db.Conexion;
import org.briancarrillo.sistema.Principal;

public class ContactoUrgenciaController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion=operaciones.NINGUNO;
    private boolean verificacion=false;
    private char[]arrayContactoUrgencia;
    private char[]arrayNumeroContacto;
    private int[]arrayCodigoPaciente;
    
    private ObservableList<ContactoUrgencia>listaContactoUrgencia;
    private ObservableList<Paciente>listaPaciente;
    
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtNumeroContacto;
    @FXML private ComboBox cmbCodigoPaciente;
    @FXML private TableView tblContactoUrgencia;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colNumeroContacto;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoPaciente.setItems(getPacientes());
        init();
    }
    
    public void cargarDatos(){
        tblContactoUrgencia.setItems(getContactosUrgencia());
        colCodigo.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, Integer>("codigoContactoUrgencia"));
        colNombres.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("apellidos"));
        colNumeroContacto.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("numeroContacto"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, Integer>("codigoPaciente"));
    }
    
    public ObservableList<ContactoUrgencia> getContactosUrgencia(){
        ArrayList<ContactoUrgencia> lista = new ArrayList<ContactoUrgencia>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarContactosUrgencia}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new ContactoUrgencia(resultado.getInt("codigoContactoUrgencia"),
                                               resultado.getString("nombres"),
                                               resultado.getString("apellidos"),
                                               resultado.getString("numeroContacto"),
                                               resultado.getInt("codigoPaciente")));
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaContactoUrgencia = FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblContactoUrgencia.getSelectionModel().getSelectedItem()!=null){
            txtCodigo.setText(String.valueOf(((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getCodigoContactoUrgencia()));
            txtNombres.setText(String.valueOf(((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getNombres()));
            txtApellidos.setText(String.valueOf(((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getApellidos()));
            txtNumeroContacto.setText(((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getNumeroContacto());
            cmbCodigoPaciente.getSelectionModel().select(buscarPaciente(((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
        }else{
            tblContactoUrgencia.getSelectionModel().clearSelection();
        }
    }
    
    public ContactoUrgencia buscarContactoUrgencia(int codigoContactoUrgencia){
        ContactoUrgencia resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarContactoUrgencia(?)}");
            procedimiento.setInt(1, codigoContactoUrgencia);
            ResultSet registro = procedimiento.executeQuery();
            
            while(registro.next()){
                resultado= new ContactoUrgencia(registro.getInt("codgioContactoUrgencia"),
                                     registro.getString("nombres"),
                                     registro.getString("apellidos"),
                                     registro.getString("numeroContacto"),
                                     registro.getInt("codigoPaciente")); 
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public Paciente buscarPaciente(int codigoMedico){
        Paciente resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPaciente(?)}");
            procedimiento.setInt(1, codigoMedico);
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
                tblContactoUrgencia.setDisable(false);
                tblContactoUrgencia.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                break;
                
            default:
                if(tblContactoUrgencia.getSelectionModel().getSelectedItem()!= null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar Contacto Urgencia", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta==JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarContactoUrgencia(?)}");
                            procedimiento.setInt(1, (((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getCodigoContactoUrgencia()));
                            procedimiento.execute();
                            listaContactoUrgencia.remove(tblContactoUrgencia.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblContactoUrgencia.getSelectionModel().select(null);
                         }catch(Exception e){
                             e.printStackTrace();
                         }
                    }else{
                        limpiarControles();
                        tblContactoUrgencia.getSelectionModel().select(null);
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
                tblContactoUrgencia.setDisable(true);
                activarControles();
                cmbCodigoPaciente.setDisable(false);
                limpiarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                tblContactoUrgencia.getSelectionModel().select(null);
                
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
                tblContactoUrgencia.setDisable(false);
                tipoDeOperacion=operaciones.NINGUNO;
                cargarDatos();
                }
                break;
        }
    }
    
    public ObservableList<Paciente> getPacientes(){
        ArrayList<Paciente>lista= new ArrayList<Paciente>();
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarPacientes()}");
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
    
    public void activarControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtNumeroContacto.setEditable(true);
    }
    
    public void desactivarControles(){
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtNumeroContacto.setEditable(false);
        cmbCodigoPaciente.setDisable(true);
    }
    
    public void limpiarControles(){
        txtCodigo.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtNumeroContacto.setText("");
        cmbCodigoPaciente.getSelectionModel().select(null);
    }
    
    public void guardar(){
        ContactoUrgencia registro = new ContactoUrgencia();
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setNumeroContacto(txtNumeroContacto.getText());
        Paciente paciente = (Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem();
        registro.setCodigoPaciente(paciente.getCodigoPaciente());
        
        try{
        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarContactoUrgencia(?,?,?,?)}");
        procedimiento.setString(1, registro.getNombres());
        procedimiento.setString(2, registro.getApellidos());
        procedimiento.setString(3, registro.getNumeroContacto());
        procedimiento.setInt(4, registro.getCodigoPaciente());
        procedimiento.execute();
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblContactoUrgencia.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setDisable(false);
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tblContactoUrgencia.setDisable(true);
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
                    tblContactoUrgencia.setDisable(false);
                    tipoDeOperacion=operaciones.NINGUNO;
                    }
                break;
        }
    
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarContactoUrgencia(?,?,?,?)}");
            ContactoUrgencia registro = ((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem());
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setNumeroContacto(txtNumeroContacto.getText());
            procedimiento.setInt(1, registro.getCodigoContactoUrgencia());
            procedimiento.setString(2, registro.getNombres());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getNumeroContacto());
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
                tblContactoUrgencia.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("");
                btnReporte.setDisable(true);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblContactoUrgencia.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                 break;
         }
     }
    
    public boolean verificarControles(){
        boolean verificado=false;
        
        if(!txtNombres.getText().equals("") && !txtApellidos.getText().equals("") && !txtNumeroContacto.getText().equals("") 
                                && cmbCodigoPaciente.getSelectionModel().getSelectedItem() != null){
            
            arrayContactoUrgencia=txtNumeroContacto.getText().toCharArray();
            
            for(int i=0; i<arrayContactoUrgencia.length; i++){
                if(arrayContactoUrgencia[i]!='0' && arrayContactoUrgencia[i]!='1' && arrayContactoUrgencia[i]!= '2' && arrayContactoUrgencia[i]!='3' && arrayContactoUrgencia[i]!='4' && arrayContactoUrgencia[i]!='5'
                        && arrayContactoUrgencia[i]!='6' && arrayContactoUrgencia[i]!='7' && arrayContactoUrgencia[i]!='8' && arrayContactoUrgencia[i]!='9'){
                    i=arrayContactoUrgencia.length;
                    verificado=false;
                    
                }else{
                    verificado=true;
                }
            }
            
            if(verificado!=true){
                JOptionPane.showMessageDialog(null, "Solamente puede ingresar números en el campo de Número de Contacto.");
            }
            
            if(verificado==true){
                
                if(txtNumeroContacto.getText().toCharArray().length !=8){
                        verificado=false;
                        JOptionPane.showMessageDialog(null, "Es obligatorio que ingrese 8 dígitos en el campo Numero de Contacto.");
                } 
            }
            
            if(verificado==true){
                for(int i=0; i<getContactosUrgencia().size();i++){
                    if(getContactosUrgencia().get(i).getNumeroContacto().equals((txtNumeroContacto.getText())) && tipoDeOperacion==operaciones.GUARDAR){
                        verificado=false;
                        JOptionPane.showMessageDialog(null, "No puede ingresar el número de contacto de otro Usuario existente.");
                        i=getContactosUrgencia().size();
                    }
                }
            }
            
                for(int i=0; i<getContactosUrgencia().size();i++){
                    if(getContactosUrgencia().get(i).getCodigoPaciente() == ((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente() && tipoDeOperacion==operaciones.GUARDAR){
                        verificado=false;
                        JOptionPane.showMessageDialog(null, "No puede crear más de un Contacto Urgencia para un mismo Paciente.");
                        i=getContactosUrgencia().size();
                    }
                }
            
        }else{
            JOptionPane.showMessageDialog(null, "Algunos de los datos solicitados están vacíos, por favor verifique.");
        }
        
        return verificado;
    }
    
    public void init(){
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
        
        txtNumeroContacto.setOnKeyTyped(new EventHandler<KeyEvent>(){
            
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                if(txtNumeroContacto.getText().toCharArray().length >7){
                    event.consume();
                }
            }
            
        });
        
    }
    
    public Principal getEscenarioPrincipal(){
     return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaPacientes(){
        escenarioPrincipal.ventanaPacientes();
    }
    
}
