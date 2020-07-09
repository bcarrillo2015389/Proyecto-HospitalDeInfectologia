 
package org.briancarrillo.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import org.briancarrillo.bean.Medico;
import org.briancarrillo.db.Conexion;
import org.briancarrillo.report.GenerarReporte;
import org.briancarrillo.sistema.Principal;

public class MedicoController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private boolean verificacion=false;
    private char[]arrayLicencia;
    
    private ObservableList<Medico> listaMedico;
    @FXML private TextField txtLicenciaMedica;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtHoraEntrada;
    @FXML private TextField txtHoraSalida;
    @FXML private TextField txtTurno;
    @FXML private TextField txtSexo;
    @FXML private TableView tblMedicos;
    
    @FXML private TableColumn colCodigoMedico;
    @FXML private TableColumn colLicenciaMedica;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colEntrada;
    @FXML private TableColumn colSalida;
    @FXML private TableColumn colTurno;
    @FXML private TableColumn colSexo;
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
        tblMedicos.setItems(getMedicos());
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<Medico,Integer>("codigoMedico"));
        colLicenciaMedica.setCellValueFactory(new PropertyValueFactory<Medico,Integer>("licenciaMedica"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Medico,String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Medico,String>("apellidos"));
        colEntrada.setCellValueFactory(new PropertyValueFactory<Medico,String>("horaEntrada"));
        colSalida.setCellValueFactory(new PropertyValueFactory<Medico,String>("horaSalida"));
        colTurno.setCellValueFactory(new PropertyValueFactory<Medico,Integer>("turnoMaximo"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Medico,String>("sexo"));
    }
    
    public ObservableList<Medico> getMedicos(){
        ArrayList<Medico> lista = new ArrayList<Medico>();
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
       
        return listaMedico =FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblMedicos.getSelectionModel().getSelectedItem()!=null){
        txtLicenciaMedica.setText(String.valueOf(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getLicenciaMedica()));
        txtNombres.setText(String.valueOf(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getNombres()));
        txtApellidos.setText(String.valueOf(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getApellidos()));
        txtHoraEntrada.setText(String.valueOf(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getHoraEntrada()));
        txtHoraSalida.setText(String.valueOf(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getHoraSalida()));
        txtTurno.setText(String.valueOf(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getTurnoMaximo()));
        txtSexo.setText(String.valueOf(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getSexo()));
        }else{
            tblMedicos.getSelectionModel().clearSelection();
        }
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
                btnReporte.setDisable(false);
                tblMedicos.setDisable(false);
                tblMedicos.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.NINGUNO;
                break;
             
            default:
                if(tblMedicos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar Medico", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta==JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarMedico(?)}");
                            procedimiento.setInt(1, ((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getCodigoMedico());
                            procedimiento.execute();
                            listaMedico.remove(tblMedicos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblMedicos.getSelectionModel().select(null);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        limpiarControles();
                        tblMedicos.getSelectionModel().select(null);
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
                tblMedicos.setDisable(true);
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblMedicos.getSelectionModel().select(null);
                
                tipoDeOperacion=operaciones.GUARDAR;
                break;
                
            case GUARDAR:
                
                try{
                verificacion=verificarControles();
                }catch(Exception e){
                    e.printStackTrace();
                }
                
                if(verificacion==true){
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tipoDeOperacion=operaciones.NINGUNO;
                    tblMedicos.setDisable(false);
                    cargarDatos();
                }
                
                break;
        }
    }
    
    public void generarReporte(){
        switch(tipoDeOperacion){
            case NINGUNO:
                tblMedicos.getSelectionModel().select(null);
                limpiarControles();
                
                imprimirReporte();
                tipoDeOperacion=operaciones.NINGUNO;
                break;
                
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                tblMedicos.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblMedicos.getSelectionModel().select(null);
            
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
        GenerarReporte.mostrarReporte("ReporteMedicos.jasper", "Reporte de Medicos", parametros);
    }
    
    
    public void desactivarControles(){
        txtLicenciaMedica.setEditable(false);
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtHoraEntrada.setEditable(false);
        txtHoraSalida.setEditable(false);
        txtTurno.setEditable(false);
        txtSexo.setEditable(false);
    }
    
    public void activarControles(){
        txtLicenciaMedica.setEditable(true);
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtHoraEntrada.setEditable(true);
        txtHoraSalida.setEditable(true);
        txtTurno.setEditable(false);
        txtSexo.setEditable(true);
    }
    
    public void limpiarControles(){
        txtLicenciaMedica.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtHoraEntrada.setText("");
        txtHoraSalida.setText("");
        txtTurno.setText("");
        txtSexo.setText("");
    }
    
    
    public void guardar(){
        Medico registro = new Medico();
        registro.setLicenciaMedica(Integer.parseInt(txtLicenciaMedica.getText()));
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setHoraEntrada(txtHoraEntrada.getText());
        registro.setHoraSalida(txtHoraSalida.getText());
        registro.setSexo(txtSexo.getText());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarMedico(?,?,?,?,?,?)}");
            procedimiento.setInt(1, registro.getLicenciaMedica());
            procedimiento.setString(2, registro.getNombres());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getHoraEntrada());
            procedimiento.setString(5, registro.getHoraSalida());
            procedimiento.setString(6, registro.getSexo());
            procedimiento.execute();
            listaMedico.add(registro);
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblMedicos.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tblMedicos.setDisable(true);
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
                    btnReporte.setText("Reporte");
                    tipoDeOperacion=operaciones.NINGUNO;
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    cargarDatos();
                    desactivarControles();
                    limpiarControles();
                    tblMedicos.setDisable(false);
                }
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarMedico(?,?,?,?,?,?,?)}");
            Medico registro = ((Medico)tblMedicos.getSelectionModel().getSelectedItem());
            registro.setLicenciaMedica(Integer.parseInt(txtLicenciaMedica.getText()));
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setHoraEntrada(txtHoraEntrada.getText());
            registro.setHoraSalida(txtHoraSalida.getText());
            registro.setSexo(txtSexo.getText());
            procedimiento.setInt(1, registro.getCodigoMedico());
            procedimiento.setInt(2, registro.getLicenciaMedica());
            procedimiento.setString(3, registro.getNombres());
            procedimiento.setString(4, registro.getApellidos());
            procedimiento.setString(5, registro.getHoraEntrada());
            procedimiento.setString(6, registro.getHoraSalida());
            procedimiento.setString(7, registro.getSexo());
            procedimiento.execute();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    public boolean verificarControles(){
        boolean verificado=false;
        
        if(!txtLicenciaMedica.getText().equals("") && !txtNombres.getText().equals("") && !txtApellidos.getText().equals("") && !txtHoraEntrada.getText().equals("") 
                && !txtHoraSalida.getText().equals("") && !txtSexo.getText().equals("")){
            
            arrayLicencia=txtLicenciaMedica.getText().toCharArray();
            
            for(int i=0; i<arrayLicencia.length; i++){
                if(arrayLicencia[i]!='0' && arrayLicencia[i]!='1' && arrayLicencia[i]!= '2' && arrayLicencia[i]!='3' && arrayLicencia[i]!='4' && arrayLicencia[i]!='5'
                        && arrayLicencia[i]!='6' && arrayLicencia[i]!='7' && arrayLicencia[i]!='8' && arrayLicencia[i]!='9'){
                    i=arrayLicencia.length;
                    verificado=false;
                    
                }else{
                    verificado=true;
                }
            }
            
            if(verificado!=true){
                JOptionPane.showMessageDialog(null, "Solamente puede ingresar números en el campo de Licencia Médica.");
            }
            
            if(verificado==true){
                for(int i=0; i<getMedicos().size();i++){
                    if(String.valueOf(getMedicos().get(i).getLicenciaMedica()).equals((txtLicenciaMedica.getText())) && tipoDeOperacion==operaciones.GUARDAR){
                        verificado=false;
                        JOptionPane.showMessageDialog(null, "No puede ingresar el número de Licencia Médica de otro Médico existente.");
                        i=getMedicos().size();
                    }
                }
            }
            
            
        }else{
            JOptionPane.showMessageDialog(null, "Algunos de los datos solicitados están vacíos, por favor verifique.");
        }
        
        return verificado;
    }

    private void init(){
        txtLicenciaMedica.setOnKeyTyped(new EventHandler<KeyEvent>(){
            
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                if(txtLicenciaMedica.getText().toCharArray().length >8){
                    event.consume();
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
        
        txtHoraEntrada.setOnKeyTyped(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                if(txtHoraEntrada.getText().toCharArray().length >9){
                    event.consume();
                }
                
                if(!Character.isDigit(caracter) && caracter != ':'){
                     event.consume();
                 }
            }
            
        });
        
        
        txtHoraSalida.setOnKeyTyped(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                char caracter = event.getCharacter().charAt(0);
                
                if(Character.isSpaceChar(caracter)){
                    event.consume();
                }
                
                if(txtHoraEntrada.getText().toCharArray().length >9){
                    event.consume();
                }
                
                if(!Character.isDigit(caracter) && caracter != ':'){
                     event.consume();
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
                
                if(txtSexo.getText().toCharArray().length >19){
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
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_VerificarTelefonoMedico}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                if(resultado.getString("telefonoPersonal") == null){
                    verificacion=false;
                }
            }
            
            if(verificacion==false){
                JOptionPane.showMessageDialog(null, "Existen médicos a los que no se les ha creado un teléfono de contacto, por favor verifique.");
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
     
    public void ventanaTelefonosMedico(){
        escenarioPrincipal.ventanaTelefonosMedico();
    }
}
