
package org.briancarrillo.report;

import java.io.InputStream;
import java.util.Map;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.briancarrillo.db.Conexion;

public class GenerarReporte {
    
    public static void mostrarReporte(String nombreReporte, String titulo, Map paremetro){
        InputStream reporte = GenerarReporte.class.getResourceAsStream(nombreReporte);
        
        try{
            JasperReport reporteMaestro = (JasperReport)JRLoader.loadObject(reporte);
            //Reporte Impreso
            JasperPrint reporteImpreso = JasperFillManager.fillReport(reporteMaestro, paremetro, Conexion.getInstancia().getConexion());
            // ver el reporte
            
            JasperViewer visor = new JasperViewer(reporteImpreso, false);
            visor.setTitle(titulo);
            visor.setVisible(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
