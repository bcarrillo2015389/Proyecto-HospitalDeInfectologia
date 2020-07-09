
package org.briancarrillo.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;
import org.briancarrillo.bean.Horario;
import org.briancarrillo.db.Conexion;
import org.briancarrillo.sistema.Principal;

public class HorarioController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private boolean verificacion=false;
    
    
    private ObservableList<Horario>listaHorario;
    @FXML private TextField txtCodigoHorario;
    @FXML private TextField txtHorarioInicio;
    @FXML private TextField txtHorarioSalida;
    @FXML private TableView tblHorarios;
    @FXML private CheckBox chbLunes;
    @FXML private CheckBox chbMartes;
    @FXML private CheckBox chbMiercoles;
    @FXML private CheckBox chbJueves;
    @FXML private CheckBox chbViernes;
    @FXML private TableColumn colCodigoHorario;
    @FXML private TableColumn colHorarioInicio;
    @FXML private TableColumn colHorarioSalida;
    @FXML private TableColumn colLunes;
    @FXML private TableColumn colMartes;
    @FXML private TableColumn colMiercoles;
    @FXML private TableColumn colJueves;
    @FXML private TableColumn colViernes;
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
        tblHorarios.setItems(getHorarios());
        colCodigoHorario.setCellValueFactory(new PropertyValueFactory<Horario,Integer>("codigoHorario"));
        colHorarioInicio.setCellValueFactory(new PropertyValueFactory<Horario,String>("horarioInicio"));
        colHorarioSalida.setCellValueFactory(new PropertyValueFactory<Horario,String>("horarioSalida"));
        colLunes.setCellValueFactory(new PropertyValueFactory<Horario,Boolean>("lunes"));
        colMartes.setCellValueFactory(new PropertyValueFactory<Horario,Boolean>("martes"));
        colMiercoles.setCellValueFactory(new PropertyValueFactory<Horario,Boolean>("miercoles"));
        colJueves.setCellValueFactory(new PropertyValueFactory<Horario,Boolean>("jueves"));
        colViernes.setCellValueFactory(new PropertyValueFactory<Horario,Boolean>("viernes"));
        
    }
    
    public ObservableList<Horario> getHorarios(){
        ArrayList<Horario>lista=new ArrayList<Horario>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarHorarios}");
            ResultSet resultado = procedimiento.executeQuery();
        
            while(resultado.next()){
                lista.add(new Horario(resultado.getInt("codigoHorario"),resultado.getString("horarioInicio"),
                                      resultado.getString("horarioSalida"), resultado.getBoolean("lunes"),
                                      resultado.getBoolean("martes"), resultado.getBoolean("miercoles"),
                                      resultado.getBoolean("jueves"), resultado.getBoolean("viernes")));
            }
           
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaHorario=FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblHorarios.getSelectionModel().getSelectedItem()!=null){
            txtCodigoHorario.setText(String.valueOf(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getCodigoHorario()));
            txtHorarioInicio.setText(String.valueOf(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getHorarioInicio()));
            txtHorarioSalida.setText(String.valueOf(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getHorarioSalida()));
            chbLunes.setSelected((((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isLunes()));
            chbMartes.setSelected((((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isMartes()));
            chbMiercoles.setSelected((((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isMiercoles()));
            chbJueves.setSelected((((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isJueves()));
            chbViernes.setSelected((((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isViernes()));
            
        }else{
            tblHorarios.getSelectionModel().clearSelection();
        }
    }
    
    public Horario buscarHorario(int codigoHorario){
        Horario resultado = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarHorario(?)}");
            procedimiento.setInt(1, codigoHorario);
            ResultSet registro = procedimiento.executeQuery();
            
            while(registro.next()){
                resultado = new Horario(registro.getInt("codigoHorario"),
                                      registro.getString("horarioInicio"),
                                      registro.getString("horarioSalida"),
                                      registro.getBoolean("lunes"),
                                      registro.getBoolean("martes"),
                                      registro.getBoolean("miercoles"),
                                      registro.getBoolean("jueves"),
                                      registro.getBoolean("viernes")
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
                tblHorarios.setDisable(false);
                tblHorarios.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                break;
            
            default:
                if(tblHorarios.getSelectionModel().getSelectedItem() != null){
                    int resultado = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar Horario", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(resultado==JOptionPane.YES_OPTION){
                       try{
                           PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarHorario(?)}");
                           procedimiento.setInt(1, (((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getCodigoHorario()));
                           procedimiento.execute();
                           
                           listaHorario.remove(tblHorarios.getSelectionModel().getSelectedIndex());
                           limpiarControles();
                           tblHorarios.getSelectionModel().select(null);
                       }catch(Exception e){
                           e.printStackTrace();
                       }
                    }else{
                        limpiarControles();
                        tblHorarios.getSelectionModel().select(null);
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
                tblHorarios.setDisable(true);
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                tblHorarios.getSelectionModel().select(null);
                
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
                tblHorarios.setDisable(false);
                cargarDatos();
                }
                break;
        }
    }
    
    public void activarControles(){
        txtHorarioInicio.setEditable(true);
        txtHorarioSalida.setEditable(true);
        chbLunes.setDisable(false);
        chbMartes.setDisable(false);
        chbMiercoles.setDisable(false);
        chbJueves.setDisable(false);
        chbViernes.setDisable(false);
    }
    
    public void desactivarControles(){
        txtHorarioInicio.setEditable(false);
        txtHorarioSalida.setEditable(false);
        chbLunes.setDisable(true);
        chbMartes.setDisable(true);
        chbMiercoles.setDisable(true);
        chbJueves.setDisable(true);
        chbViernes.setDisable(true);
    }
    
    public void limpiarControles(){
        txtCodigoHorario.setText("");
        txtHorarioInicio.setText("");
        txtHorarioSalida.setText("");
        chbLunes.setSelected(false);
        chbMartes.setSelected(false);
        chbMiercoles.setSelected(false);
        chbJueves.setSelected(false);
        chbViernes.setSelected(false);
    }
    
    public void guardar(){
        Horario registro = new Horario();
        registro.setHorarioInicio(txtHorarioInicio.getText());
        registro.setHorarioSalida(txtHorarioSalida.getText());
        registro.setLunes(chbLunes.isSelected());
        registro.setMartes(chbMartes.isSelected());
        registro.setMiercoles(chbMiercoles.isSelected());
        registro.setJueves(chbJueves.isSelected());
        registro.setViernes(chbViernes.isSelected());
        
        try{
        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarHorario(?,?,?,?,?,?,?)}");
        procedimiento.setString(1, registro.getHorarioInicio());
        procedimiento.setString(2, registro.getHorarioSalida());
        procedimiento.setBoolean(3, registro.isLunes());
        procedimiento.setBoolean(4, registro.isMartes());
        procedimiento.setBoolean(5, registro.isMiercoles());
        procedimiento.setBoolean(6, registro.isJueves());
        procedimiento.setBoolean(7, registro.isViernes());
        procedimiento.execute();
        
        listaHorario.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblHorarios.getSelectionModel().getSelectedItem()!=null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setDisable(false);
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tblHorarios.setDisable(true);
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
                    btnReporte.setText("");
                    btnReporte.setDisable(true);
                    tipoDeOperacion=operaciones.NINGUNO;
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    cargarDatos();
                    desactivarControles();
                    limpiarControles();
                    tblHorarios.setDisable(false);
                }
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarHorario(?,?,?,?,?,?,?,?)}");
            Horario registro = ((Horario)tblHorarios.getSelectionModel().getSelectedItem());
            registro.setHorarioInicio(txtHorarioInicio.getText());
            registro.setHorarioSalida(txtHorarioSalida.getText());
            registro.setLunes(chbLunes.isSelected());
            registro.setMartes(chbMartes.isSelected());
            registro.setMiercoles(chbMiercoles.isSelected());
            registro.setJueves(chbJueves.isSelected());
            registro.setViernes(chbViernes.isSelected());
            
            procedimiento.setInt(1, registro.getCodigoHorario());
            procedimiento.setString(2, registro.getHorarioInicio());
            procedimiento.setString(3, registro.getHorarioSalida());
            procedimiento.setBoolean(4, registro.isLunes());
            procedimiento.setBoolean(5, registro.isMartes());
            procedimiento.setBoolean(6, registro.isMiercoles());
            procedimiento.setBoolean(7, registro.isJueves());
            procedimiento.setBoolean(8, registro.isViernes());
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
                tblHorarios.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("");
                btnReporte.setDisable(true);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblHorarios.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                break;
        }
    }
    
    public boolean verificarControles(){
        boolean verificado=false;
        char[]arrayHoraInicio;
        char[]arrayHoraSalida;
        
        if(!txtHorarioInicio.getText().equals("") && !txtHorarioSalida.getText().equals("")){
            verificado=true;
            
            if(chbLunes.isSelected()==false && chbMartes.isSelected()==false && chbMiercoles.isSelected()==false && chbJueves.isSelected()==false && chbViernes.isSelected()==false){
                verificado=false;
                JOptionPane.showMessageDialog(null, "Debe de seleccionar como mínimo un día de la semana.");
            }
            
            arrayHoraInicio=txtHorarioInicio.getText().toCharArray();
            arrayHoraSalida=txtHorarioSalida.getText().toCharArray();
            
            if(arrayHoraInicio.length == 5 && arrayHoraSalida.length == 5){
                if(!Character.isDigit(arrayHoraInicio[0]) && !Character.isDigit(arrayHoraInicio[1]) && arrayHoraInicio[2]!=':' && !Character.isDigit(arrayHoraInicio[3]) && !Character.isDigit(arrayHoraInicio[4])
                        && !Character.isDigit(arrayHoraSalida[0]) && !Character.isDigit(arrayHoraSalida[1]) && arrayHoraSalida[2]!=':' && !Character.isDigit(arrayHoraSalida[3]) && !Character.isDigit(arrayHoraSalida[4])){
                    verificado=false;
                    JOptionPane.showMessageDialog(null, "El formato de hora es incorrecto en alguno de los campos. Recuerde utilizar el formato hh-MM");
                    
                }
   
            }else{
                JOptionPane.showMessageDialog(null, "El formato de hora es incorrecto en alguno de los campos. Recuerde utilizar el formato hh-MM");
                verificado=false;
            }
            
        }else{
            JOptionPane.showMessageDialog(null,"Los datos solicitados están vacíos, por favor verifique.");
        }
        
        return verificado;
    }
    
    public void init(){
        txtHorarioInicio.setOnKeyTyped(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                if(txtHorarioInicio.getText().toCharArray().length >9){
                    event.consume();
                }
                
                if(!Character.isDigit(caracter) && caracter != ':'){
                     event.consume();
                 }
            }
            
        });
        
        
        txtHorarioSalida.setOnKeyTyped(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                if(txtHorarioSalida.getText().toCharArray().length >9){
                    event.consume();
                }
                
                if(!Character.isDigit(caracter) && caracter != ':'){
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
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
}
