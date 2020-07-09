
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
import org.briancarrillo.bean.Area;
import org.briancarrillo.db.Conexion;
import org.briancarrillo.sistema.Principal;

public class AreaController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion=operaciones.NINGUNO;
    private boolean verificacion=false;
    
    private ObservableList<Area>listaArea;
    @FXML private TextField txtCodigoArea;
    @FXML private TextField txtNombreArea;
    @FXML private TableView tblAreas;
    @FXML private TableColumn colCodigoArea;
    @FXML private TableColumn colNombreArea;
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
        tblAreas.setItems(getAreas());
        colCodigoArea.setCellValueFactory(new PropertyValueFactory<Area,Integer>("codigoArea"));
        colNombreArea.setCellValueFactory(new PropertyValueFactory<Area,String>("nombreArea"));
    }
    
    public ObservableList<Area> getAreas(){
        ArrayList<Area> lista= new ArrayList<Area>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarAreas}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Area(resultado.getInt("codigoArea"), resultado.getString("nombreArea")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaArea = FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblAreas.getSelectionModel().getSelectedItem()!=null){
            txtCodigoArea.setText(String.valueOf(((Area)tblAreas.getSelectionModel().getSelectedItem()).getCodigoArea()));
            txtNombreArea.setText(String.valueOf(((Area)tblAreas.getSelectionModel().getSelectedItem()).getNombreArea()));
        }else{
            tblAreas.getSelectionModel().clearSelection();
        }
    }
    
    public Area buscarArea(int codigoArea){
        Area resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarArea(?)}");
            procedimiento.setInt(1, codigoArea);
            ResultSet registro = procedimiento.executeQuery();
        
        while(registro.next()){
            resultado = new Area(registro.getInt("codigoArea"),
                                 registro.getString("nombreArea"));
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
                tblAreas.setDisable(false);
                tblAreas.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                break;
                
            default:
                if(tblAreas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar Area", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta==JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarArea(?)}");
                            procedimiento.setInt(1, ((Area)tblAreas.getSelectionModel().getSelectedItem()).getCodigoArea());
                            procedimiento.execute();
                            listaArea.remove(tblAreas.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblAreas.getSelectionModel().select(null);
                        }catch(Exception e){
                            e.printStackTrace();
                        } 
                    }else{
                        limpiarControles();
                        tblAreas.getSelectionModel().select(null);
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
                tblAreas.setDisable(true);
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                tblAreas.getSelectionModel().select(null);
                
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
                tblAreas.setDisable(false);
                cargarDatos();
                }
                break;
        }
    
    }
    

    public void activarControles(){
        txtNombreArea.setEditable(true);
    }
    
    public void desactivarControles(){
        txtNombreArea.setEditable(false);
    }
    
    public void limpiarControles(){
        txtNombreArea.setText("");
        txtCodigoArea.setText("");
    }
    
    public void guardar(){
        Area registro = new Area();
        registro.setNombreArea(txtNombreArea.getText());
        
        try{
        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarArea(?)}");
        procedimiento.setString(1, registro.getNombreArea());
        procedimiento.execute();
        listaArea.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblAreas.getSelectionModel().getSelectedItem()!=null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnReporte.setDisable(false);
                    activarControles();
                    tblAreas.setDisable(true);
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
                    tblAreas.setDisable(false);
                }
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarArea(?,?)}");
            Area registro = ((Area)tblAreas.getSelectionModel().getSelectedItem());
            registro.setNombreArea(txtNombreArea.getText());
            procedimiento.setInt(1, registro.getCodigoArea());
            procedimiento.setString(2, registro.getNombreArea());
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
                tblAreas.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("");
                btnReporte.setDisable(true);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblAreas.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                 break;
         }
     }
    
    public boolean verificarControles(){
        boolean verificado=false;
        
        if(!txtNombreArea.getText().equals("")){
          verificado=true;
        }else{
            JOptionPane.showMessageDialog(null,"Los datos solicitados están vacíos, por favor verifique.");
        }
        
        return verificado;
    }
    
    private void init(){
        
        txtNombreArea.setOnKeyTyped(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(txtNombreArea.getText().equals("") && Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                if(txtNombreArea.getText().toCharArray().length >44){
                    event.consume();
                }
                 
                 if(!Character.isAlphabetic(caracter) && !Character.isSpaceChar(caracter)){
                     event.consume();
                 }
            }
        });
        
        txtNombreArea.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(txtNombreArea.getText().equals(" ")){
                    txtNombreArea.setText("");
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
