
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
import org.briancarrillo.bean.Especialidad;
import org.briancarrillo.db.Conexion;
import org.briancarrillo.sistema.Principal;

public class EspecialidadController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion=operaciones.NINGUNO;
    private boolean verificacion=false;
    
    private ObservableList<Especialidad> listaEspecialidad;
    @FXML private TextField txtCodigoEspecialidad;
    @FXML private TextField txtNombreEspecialidad;
    @FXML private TableView<Especialidad>tblEspecialidades;
    @FXML private TableColumn colCodigoEspecialidad;
    @FXML private TableColumn colNombreEspecialidad;
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
        tblEspecialidades.setItems(getEspecialidades());
        colCodigoEspecialidad.setCellValueFactory(new PropertyValueFactory<Especialidad,Integer>("codigoEspecialidad"));
        colNombreEspecialidad.setCellValueFactory(new PropertyValueFactory<Especialidad, String>("nombreEspecialidad"));
    }
    public ObservableList<Especialidad> getEspecialidades(){
        ArrayList<Especialidad> lista = new ArrayList<Especialidad>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarEspecialidades}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Especialidad(resultado.getInt("codigoEspecialidad"), resultado.getString("nombreEspecialidad")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaEspecialidad = FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblEspecialidades.getSelectionModel().getSelectedItem()!=null){
            txtCodigoEspecialidad.setText(String.valueOf(((Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem()).getCodigoEspecialidad()));
            txtNombreEspecialidad.setText(String.valueOf(((Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem()).getNombreEspecialidad()));
        }else{
            tblEspecialidades.getSelectionModel().clearSelection();
        }
        
    }
    
    public Especialidad buscarEspecialidad(int codigoEspecialidad){
        Especialidad resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarEspecialidad(?)}");
            ResultSet registro = procedimiento.executeQuery();
        
            while(registro.next()){
                resultado = new Especialidad(registro.getInt("codigoEspecialidad"), registro.getString("nombreEspecialidad"));
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
                    tblEspecialidades.setDisable(false);
                    tblEspecialidades.getSelectionModel().select(null);
                    
                    tipoDeOperacion=operaciones.NINGUNO;
                    break;
                    
                default:
                    if(tblEspecialidades.getSelectionModel().getSelectedItem() != null){
                        int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar Especialidad", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if(respuesta==JOptionPane.YES_OPTION){
                            try{
                                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarEspecialidad(?)}");
                                procedimiento.setInt(1, (((Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem()).getCodigoEspecialidad()));
                                procedimiento.execute();
                                
                                listaEspecialidad.remove(tblEspecialidades.getSelectionModel().getSelectedIndex());
                                limpiarControles();
                                tblEspecialidades.getSelectionModel().select(null);
                            }catch(Exception e){
                            
                            }
                        }else{
                            limpiarControles();
                            tblEspecialidades.getSelectionModel().select(null);
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
                tblEspecialidades.setDisable(true);
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                tblEspecialidades.getSelectionModel().select(null);
                
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
                tblEspecialidades.setDisable(false);
                cargarDatos();
                }
                break;
        }
    }
    
    
    public void desactivarControles(){
        txtNombreEspecialidad.setEditable(false);
    }
    
    public void activarControles(){
        txtNombreEspecialidad.setEditable(true);
    }
    
    public void limpiarControles(){
        txtNombreEspecialidad.setText("");
        txtCodigoEspecialidad.setText("");
    }
    
    public void guardar(){
        Especialidad registro = new Especialidad();
        registro.setNombreEspecialidad(txtNombreEspecialidad.getText());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarEspecialidad(?)}");
            procedimiento.setString(1, registro.getNombreEspecialidad());
            procedimiento.execute();
            listaEspecialidad.add(registro);
        
        }catch(Exception e){
        e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblEspecialidades.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setDisable(false);
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tblEspecialidades.setDisable(true);
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
                tblEspecialidades.setDisable(false);
                }
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarEspecialidad(?,?)}");
            Especialidad registro = ((Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem());
            registro.setNombreEspecialidad(txtNombreEspecialidad.getText());
            procedimiento.setInt(1, registro.getCodigoEspecialidad());
            procedimiento.setString(2, registro.getNombreEspecialidad());
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
            tblEspecialidades.setDisable(false);
            btnEditar.setText("Editar");
            btnReporte.setText("");
                btnReporte.setDisable(true);
            btnNuevo.setDisable(false);
            btnEliminar.setDisable(false);
            tblEspecialidades.getSelectionModel().select(null);
            
            tipoDeOperacion=operaciones.NINGUNO;
            break;
            
        }
    }
    
    public boolean verificarControles(){
        boolean verificado=false;
        
        if(!txtNombreEspecialidad.getText().equals("")){
            verificado=true;
        }else{
            JOptionPane.showMessageDialog(null,"Los datos solicitados están vacíos, por favor verifique.");
        }
        
        return verificado;
    }
    
    public void init(){
        txtNombreEspecialidad.setOnKeyTyped(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(txtNombreEspecialidad.getText().equals("") && Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                 if(txtNombreEspecialidad.getText().toCharArray().length >44){
                    event.consume();
                }
                 
                 if(!Character.isAlphabetic(caracter) && !Character.isSpaceChar(caracter)){
                     event.consume();
                 }
            }
        });
        
        txtNombreEspecialidad.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(txtNombreEspecialidad.getText().equals(" ")){
                    txtNombreEspecialidad.setText("");
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
