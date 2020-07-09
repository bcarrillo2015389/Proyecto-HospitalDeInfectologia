
package org.briancarrillo.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.briancarrillo.bean.Area;
import org.briancarrillo.bean.Cargo;
import org.briancarrillo.bean.ResponsableTurno;
import org.briancarrillo.db.Conexion;
import org.briancarrillo.sistema.Principal;

public class ResponsableTurnoController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion=operaciones.NINGUNO;
    private boolean verificacion=false;
    private char[]arrayTelefonoPersonal;
 
    private ObservableList<ResponsableTurno>listaResponsableTurno;
    private ObservableList<Area>listaArea;
    private ObservableList<Cargo>listaCargo;
    
    @FXML private TextField txtCodigoResponsableTurno;
    @FXML private TextField txtNombreResponsable;
    @FXML private TextField txtApellidosResponsable;
    @FXML private TextField txtTelefonoPersonal;
    @FXML private ComboBox cmbCodigoArea;
    @FXML private ComboBox cmbCodigoCargo;
    @FXML private TableView tblResponsablesTurno;
    @FXML private TableColumn colCodigoResponsableTurno;
    @FXML private TableColumn colNombreResponsable;
    @FXML private TableColumn colApellidosResponsable;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colCodigoArea;
    @FXML private TableColumn colCodigoCargo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
     
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoArea.setItems(getAreas());
        cmbCodigoCargo.setItems(getCargos());
        init();
    }
    
    public void cargarDatos(){
        tblResponsablesTurno.setItems(getResponsablesTurno());
        colCodigoResponsableTurno.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoResponsableTurno"));
        colNombreResponsable.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, String>("nombreResponsable"));
        colApellidosResponsable.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, String>("apellidosResponsable"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, String>("telefonoPersonal"));
        colCodigoArea.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoArea"));
        colCodigoCargo.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoCargo"));
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
    
    public void seleccionarElemento(){
        if(tblResponsablesTurno.getSelectionModel().getSelectedItem() != null){
            txtCodigoResponsableTurno.setText(String.valueOf(((ResponsableTurno)tblResponsablesTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno()));
            txtNombreResponsable.setText(String.valueOf(((ResponsableTurno)tblResponsablesTurno.getSelectionModel().getSelectedItem()).getNombreResponsable()));
            txtApellidosResponsable.setText(String.valueOf(((ResponsableTurno)tblResponsablesTurno.getSelectionModel().getSelectedItem()).getApellidosResponsable()));
            txtTelefonoPersonal.setText(String.valueOf(((ResponsableTurno)tblResponsablesTurno.getSelectionModel().getSelectedItem()).getTelefonoPersonal()));
            cmbCodigoArea.getSelectionModel().select(buscarArea(((ResponsableTurno)tblResponsablesTurno.getSelectionModel().getSelectedItem()).getCodigoArea()));
            cmbCodigoCargo.getSelectionModel().select(buscarCargo(((ResponsableTurno)tblResponsablesTurno.getSelectionModel().getSelectedItem()).getCodigoCargo()));
        }else{
            tblResponsablesTurno.getSelectionModel().clearSelection();
        }
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
    
    public Area buscarArea(int codigoArea){
        Area resultado = null;
        try{
           
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarArea(?)}");
            procedimiento.setInt(1, codigoArea);
            ResultSet registro = procedimiento.executeQuery();
            
            while(registro.next()){
                resultado= new Area(registro.getInt("codigoArea"),
                                    registro.getString("nombreArea"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public Cargo buscarCargo(int codigoCargo){
        Cargo resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarCargo(?)}");
            procedimiento.setInt(1, codigoCargo);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado=new Cargo(registro.getInt("codigoCargo"),
                                            registro.getString("nombreCargo"));       
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
                tblResponsablesTurno.setDisable(false);
                tblResponsablesTurno.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                break;
                
            default:
                if(tblResponsablesTurno.getSelectionModel().getSelectedItem()!= null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar Responsable de Turno", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta==JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarResponsableTurno(?)}");
                            procedimiento.setInt(1, (((ResponsableTurno)tblResponsablesTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno()));
                            procedimiento.execute();
                            listaResponsableTurno.remove(tblResponsablesTurno.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblResponsablesTurno.getSelectionModel().select(null);
                         }catch(Exception e){
                             e.printStackTrace();
                         }
                    }else{
                        limpiarControles();
                        tblResponsablesTurno.getSelectionModel().select(null);
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
                tblResponsablesTurno.setDisable(true);
                limpiarControles();
                activarControles();
                cmbCodigoArea.setDisable(false);
                cmbCodigoCargo.setDisable(false);
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                tblResponsablesTurno.getSelectionModel().select(null);
                
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
                   tblResponsablesTurno.setDisable(false);
                    cargarDatos();
                }
                break;
        }
    }
    
    public ObservableList<Area>getAreas(){
        ArrayList<Area>lista = new ArrayList<Area>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarAreas}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Area(resultado.getInt("codigoArea"),
                                    resultado.getString("nombreArea")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaArea = FXCollections.observableList(lista);
    }
    
    public ObservableList<Cargo>getCargos(){
        ArrayList<Cargo>lista = new ArrayList<Cargo>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarCargos}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Cargo(resultado.getInt("codigoCargo"),
                                    resultado.getString("nombreCargo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaCargo = FXCollections.observableList(lista);
    }
    
    public void activarControles(){
        txtNombreResponsable.setEditable(true);
        txtApellidosResponsable.setEditable(true);
        txtTelefonoPersonal.setEditable(true);
    }
    
    public void desactivarControles(){
        txtNombreResponsable.setEditable(false);
        txtApellidosResponsable.setEditable(false);
        txtTelefonoPersonal.setEditable(false);
        cmbCodigoArea.setDisable(true);
        cmbCodigoCargo.setDisable(true);
    }
    
    public void limpiarControles(){
        txtCodigoResponsableTurno.setText("");
        txtNombreResponsable.setText("");
        txtApellidosResponsable.setText("");
        txtTelefonoPersonal.setText("");
        cmbCodigoArea.getSelectionModel().select(null);
        cmbCodigoCargo.getSelectionModel().select(null);
    }
    
    public void guardar(){
        ResponsableTurno registro = new ResponsableTurno();
        registro.setNombreResponsable(txtNombreResponsable.getText());
        registro.setApellidosResponsable(txtApellidosResponsable.getText());
        registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
        Area area = (Area)cmbCodigoArea.getSelectionModel().getSelectedItem();
        registro.setCodigoArea(area.getCodigoArea());
        Cargo cargo = (Cargo)cmbCodigoCargo.getSelectionModel().getSelectedItem();
        registro.setCodigoCargo(cargo.getCodigoCargo());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarResponsableTurno(?,?,?,?,?)}");
            procedimiento.setString(1, registro.getNombreResponsable());
            procedimiento.setString(2, registro.getApellidosResponsable());
            procedimiento.setString(3, registro.getTelefonoPersonal());
            procedimiento.setInt(4, registro.getCodigoArea());
            procedimiento.setInt(5, registro.getCodigoCargo());
            procedimiento.execute();
            listaResponsableTurno.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblResponsablesTurno.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setDisable(false);
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tblResponsablesTurno.setDisable(true);
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
                    tblResponsablesTurno.setDisable(false);
                }
                break;
        }
    
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarResponsableTurno(?,?,?,?)}");
            ResponsableTurno registro = ((ResponsableTurno)tblResponsablesTurno.getSelectionModel().getSelectedItem());
            registro.setNombreResponsable(txtNombreResponsable.getText());
            registro.setApellidosResponsable(txtApellidosResponsable.getText());
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            procedimiento.setInt(1, registro.getCodigoResponsableTurno());
            procedimiento.setString(2, registro.getNombreResponsable());
            procedimiento.setString(3, registro.getApellidosResponsable());
            procedimiento.setString(4, registro.getTelefonoPersonal());
            procedimiento.execute();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
     public void generarReporte(){
         switch(tipoDeOperacion){
             case NINGUNO:
                 
                 break;
                 
             default:
                desactivarControles();
                limpiarControles();
                tblResponsablesTurno.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("");
                btnReporte.setDisable(true);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblResponsablesTurno.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                 break;
         }
     }
     
     public boolean verificarControles(){
         boolean verificado=false;
         
         if(!txtNombreResponsable.getText().equals("") && !txtApellidosResponsable.getText().equals("") && !txtTelefonoPersonal.getText().equals("") && cmbCodigoArea.getSelectionModel().getSelectedItem() != null
                 && cmbCodigoCargo.getSelectionModel().getSelectedItem() != null){
             
             arrayTelefonoPersonal=txtTelefonoPersonal.getText().toCharArray();
             
             for(int i=0; i<arrayTelefonoPersonal.length; i++){
                if(arrayTelefonoPersonal[i]!='1' && arrayTelefonoPersonal[i]!= '2' && arrayTelefonoPersonal[i]!='3' && arrayTelefonoPersonal[i]!='4' && arrayTelefonoPersonal[i]!='5'
                        && arrayTelefonoPersonal[i]!='6' && arrayTelefonoPersonal[i]!='7' && arrayTelefonoPersonal[i]!='8' && arrayTelefonoPersonal[i]!='9'){
                    i=arrayTelefonoPersonal.length;
                    verificado=false;
                    
                }else{
                    verificado=true;
                }
            }
             
            if (verificado!=true){
                JOptionPane.showMessageDialog(null, "Solamente puede ingresar números en el campo de Telefono Personal.");
            }
            
            if(verificado==true){
                
                if(txtTelefonoPersonal.getText().toCharArray().length !=8){
                        verificado=false;
                        JOptionPane.showMessageDialog(null, "Es obligatorio que ingrese 8 dígitos en el campo Numero de Contacto.");
                } 
            }
            
            if(verificado==true){
                for(int i=0; i<getResponsablesTurno().size();i++){
                    if(getResponsablesTurno().get(i).getTelefonoPersonal().equals((txtTelefonoPersonal.getText())) && tipoDeOperacion==operaciones.GUARDAR){
                        verificado=false;
                        JOptionPane.showMessageDialog(null, "No puede ingresar el número de telefono personal de otro Usuario existente.");
                        i=getResponsablesTurno().size();
                    }
                }
            }
             
             
         }else{
             JOptionPane.showMessageDialog(null, "Algunos de los datos solicitados están vacíos, por favor verifique.");
         }
         
         return verificado;
     }
     
     public void init(){
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
         
         txtNombreResponsable.setOnKeyTyped(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(txtNombreResponsable.getText().equals("") && Character.isSpaceChar(caracter)){
                    event.consume();
                    
                }
                
                if(txtNombreResponsable.getText().toCharArray().length >74){
                    event.consume();
                }
                 
                 if(!Character.isAlphabetic(caracter) && !Character.isSpaceChar(caracter)){
                     event.consume();
                 }
            }
            
        });
        
        txtNombreResponsable.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(txtNombreResponsable.getText().equals(" ")){
                    txtNombreResponsable.setText("");
                    JOptionPane.showMessageDialog(null, "No es posible utilizar espacios en blanco al inicio de los datos. Intente de nuevo.");
                }
            }
        });
        
        txtApellidosResponsable.setOnKeyTyped(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(txtApellidosResponsable.getText().equals("") && Character.isSpaceChar(caracter)){
                    event.consume();
                    
                }
                
                if(txtApellidosResponsable.getText().toCharArray().length >44){
                    event.consume();
                }
                 
                 if(!Character.isAlphabetic(caracter) && !Character.isSpaceChar(caracter)){
                     event.consume();
                 }
            }
            
        });
        
        txtApellidosResponsable.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(txtApellidosResponsable.getText().equals(" ")){
                    txtApellidosResponsable.setText("");
                    JOptionPane.showMessageDialog(null, "No es posible utilizar espacios en blanco al inicio de los datos. Intente de nuevo.");
                }
            }
        });
     }
    
    public Principal getEscenarioPrincipal(){
        return escenarioPrincipal;
    }
    
    public void setEscenarioPrincipal(Principal escenarioPrincipal){
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
}
