
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
import org.briancarrillo.bean.Cargo;
import org.briancarrillo.db.Conexion;
import org.briancarrillo.sistema.Principal;

public class CargoController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private boolean verificacion=false;
    
    private ObservableList<Cargo>listaCargo;
    @FXML private TextField txtCodigoCargo;
    @FXML private TextField txtNombreCargo;
    @FXML private TableView tblCargos;
    @FXML private TableColumn colCodigoCargo;
    @FXML private TableColumn colNombreCargo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        init();
    }
    
    public void cargarDatos(){
        tblCargos.setItems(getCargos());
        colCodigoCargo.setCellValueFactory(new PropertyValueFactory<Cargo,Integer>("codigoCargo"));
        colNombreCargo.setCellValueFactory(new PropertyValueFactory<Cargo,String>("nombreCargo"));
    }
    
    public ObservableList<Cargo> getCargos(){
        ArrayList<Cargo>lista=new ArrayList<Cargo>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarCargos}");
            ResultSet resultado = procedimiento.executeQuery();
        
            while(resultado.next()){
                lista.add(new Cargo(resultado.getInt("codigoCargo"),resultado.getString("nombreCargo")));
            }
           
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaCargo=FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblCargos.getSelectionModel().getSelectedItem()!=null){
            txtCodigoCargo.setText(String.valueOf(((Cargo)tblCargos.getSelectionModel().getSelectedItem()).getCodigoCargo()));
            txtNombreCargo.setText(String.valueOf(((Cargo)tblCargos.getSelectionModel().getSelectedItem()).getNombreCargo()));
        }else{
            tblCargos.getSelectionModel().clearSelection();
        }
    }
    
    public Cargo buscarCargo(int codigoCargo){
        Cargo resultado = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarCargo(?)}");
            procedimiento.setInt(1, codigoCargo);
            ResultSet registro = procedimiento.executeQuery();
            
            while(registro.next()){
                resultado = new Cargo(registro.getInt("codigoCargo"),
                                      registro.getString("nombreCargo")
                                );
            }
        }catch(Exception e){
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
                tblCargos.setDisable(false);
                tblCargos.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                break;
            
            default:
                if(tblCargos.getSelectionModel().getSelectedItem() != null){
                    int resultado = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar Cargo", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(resultado==JOptionPane.YES_OPTION){
                       try{
                           PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarCargo(?)}");
                           procedimiento.setInt(1, (((Cargo)tblCargos.getSelectionModel().getSelectedItem()).getCodigoCargo()));
                           procedimiento.execute();
                           
                           listaCargo.remove(tblCargos.getSelectionModel().getSelectedIndex());
                           limpiarControles();
                           tblCargos.getSelectionModel().select(null);
                       }catch(Exception e){
                           e.printStackTrace();
                       }
                    }else{
                        limpiarControles();
                        tblCargos.getSelectionModel().select(null);
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
                tblCargos.setDisable(true);
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                tblCargos.getSelectionModel().select(null);
                
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
                tblCargos.setDisable(false);
                cargarDatos();
                }
                break;
        }
    }

    public void activarControles(){
        txtNombreCargo.setEditable(true);
    }
    
    public void desactivarControles(){
        txtNombreCargo.setEditable(false);
    }
    
    public void limpiarControles(){
        txtCodigoCargo.setText("");
        txtNombreCargo.setText("");
    }
    
    public void guardar(){
        Cargo registro = new Cargo();
        registro.setNombreCargo(txtNombreCargo.getText());
        
        try{
        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarCargo(?)}");
        procedimiento.setString(1, registro.getNombreCargo());
        procedimiento.execute();
        
        listaCargo.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblCargos.getSelectionModel().getSelectedItem()!=null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setDisable(false);
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tblCargos.setDisable(true);
                    tipoDeOperacion=operaciones.ACTUALIZAR;
                    break;
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }
                break;
                
            case ACTUALIZAR:
                verificacion=verificarControles();
                
                if(verificacion==true){
                    actualizar();
                    btnEditar.setText("Editar");
                    btnReporte.setDisable(true);
                    btnReporte.setText("");
                    tipoDeOperacion=operaciones.NINGUNO;
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    cargarDatos();
                    desactivarControles();
                    limpiarControles();
                    tblCargos.setDisable(false);
                }
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarCargo(?,?)}");
            Cargo registro = ((Cargo)tblCargos.getSelectionModel().getSelectedItem());
            registro.setNombreCargo(txtNombreCargo.getText());
            procedimiento.setInt(1, registro.getCodigoCargo());
            procedimiento.setString(2, registro.getNombreCargo());
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
                tblCargos.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("");
                btnReporte.setDisable(true);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblCargos.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                break;
        }
    }
    
    public boolean verificarControles(){
        boolean verificado=false;
        
        if(!txtNombreCargo.getText().equals("")){
            verificado=true;
        }else{
            JOptionPane.showMessageDialog(null,"Los datos solicitados están vacíos, por favor verifique.");
        }
        
        return verificado;
    }
    
    public void init(){
        txtNombreCargo.setOnKeyTyped(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
              char caracter = event.getCharacter().charAt(0);
              
                if(txtNombreCargo.getText().equals("") && Character.isSpaceChar(caracter)){
                     event.consume();
                }
              
                if(txtNombreCargo.getText().toCharArray().length >44){
                    event.consume();
                }
                 
                 if(!Character.isAlphabetic(caracter) && !Character.isSpaceChar(caracter)){
                     event.consume();
                 }
            }
        });
        
        txtNombreCargo.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(txtNombreCargo.getText().equals(" ")){
                    txtNombreCargo.setText("");
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
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
}
