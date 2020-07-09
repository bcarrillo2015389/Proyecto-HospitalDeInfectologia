
package org.briancarrillo.bean;

import java.util.Date;

public class Parqueo {
    private int codigoParqueo;
    private String espacioParqueo;
    private Date fecha;

    public Parqueo() {
    }

    public Parqueo(int codigoParqueo, String espacioParqueo, Date fecha) {
        this.codigoParqueo = codigoParqueo;
        this.espacioParqueo = espacioParqueo;
        this.fecha = fecha;
    }

    public int getCodigoParqueo() {
        return codigoParqueo;
    }

    public void setCodigoParqueo(int codigoParqueo) {
        this.codigoParqueo = codigoParqueo;
    }

    public String getEspacioParqueo() {
        return espacioParqueo;
    }

    public void setEspacioParqueo(String espacioParqueo) {
        this.espacioParqueo = espacioParqueo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    public String toString(){
        return getCodigoParqueo() + " | " + getEspacioParqueo();
    }
    
    
}
