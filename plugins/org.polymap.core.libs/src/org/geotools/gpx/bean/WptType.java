//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-hudson-3037-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.07.27 at 11:06:51 PM CDT 
//
package org.geotools.gpx.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 *
 *                 wpt represents a waypoint, point of interest, or named feature on a map.
 *
 *
 * <p>Java class for wptType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="wptType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ele" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="magvar" type="{http://www.topografix.com/GPX/1/1}degreesType" minOccurs="0"/>
 *         &lt;element name="geoidheight" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="src" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="link" type="{http://www.topografix.com/GPX/1/1}linkType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="sym" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fix" type="{http://www.topografix.com/GPX/1/1}fixType" minOccurs="0"/>
 *         &lt;element name="sat" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="hdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="vdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="pdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="ageofdgpsdata" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="dgpsid" type="{http://www.topografix.com/GPX/1/1}dgpsStationType" minOccurs="0"/>
 *         &lt;element name="extensions" type="{http://www.topografix.com/GPX/1/1}extensionsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="lat" use="required" type="{http://www.topografix.com/GPX/1/1}latitudeType" />
 *       &lt;attribute name="lon" use="required" type="{http://www.topografix.com/GPX/1/1}longitudeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
public class WptType {
    protected double ele = Double.NaN;
    protected Calendar time;
    protected double magvar = Double.NaN;
    protected double geoidheight = Double.NaN;
    protected String name;
    protected String cmt;
    protected String desc;
    protected String src;
    protected List<LinkType> link;
    protected String sym;
    protected String type;
    protected String fix;
    protected int sat = -1;
    protected double hdop = Double.NaN;
    protected double vdop = Double.NaN;
    protected double pdop = Double.NaN;
    protected double ageofdgpsdata = Double.NaN;
    protected int dgpsid = -1;
    protected ExtensionsType extensions;
    protected double lat = Double.NaN;
    protected double lon = Double.NaN;

    /**
     * Gets the value of the ele property.
     *
     */
    public double getEle() {
        return ele;
    }

    /**
     * Sets the value of the ele property.
     *
     */
    public void setEle(double value) {
        this.ele = value;
    }

    /**
     * Gets the value of the time property.
     *
     */
    public Calendar getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     *
     */
    public void setTime(Calendar value) {
        this.time = value;
    }

    /**
     * Gets the value of the magvar property.
     *
     */
    public double getMagvar() {
        return magvar;
    }

    /**
     * Sets the value of the magvar property.
     *
     */
    public void setMagvar(double value) {
        this.magvar = value;
    }

    /**
     * Gets the value of the geoidheight property.
     *
     */
    public double getGeoidheight() {
        return geoidheight;
    }

    /**
     * Sets the value of the geoidheight property.
     *
     */
    public void setGeoidheight(double value) {
        this.geoidheight = value;
    }

    /**
     * Gets the value of the name property.
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the cmt property.
     *
     */
    public String getCmt() {
        return cmt;
    }

    /**
     * Sets the value of the cmt property.
     *
     */
    public void setCmt(String value) {
        this.cmt = value;
    }

    /**
     * Gets the value of the desc property.
     *
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the value of the desc property.
     *
     */
    public void setDesc(String value) {
        this.desc = value;
    }

    /**
     * Gets the value of the src property.
     *
     */
    public String getSrc() {
        return src;
    }

    /**
     * Sets the value of the src property.
     *
     */
    public void setSrc(String value) {
        this.src = value;
    }

    /**
     * Gets the value of the link property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the link property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLink().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LinkType }
     *
     *
     */
    public List<LinkType> getLink() {
        if (link == null) {
            link = new ArrayList<LinkType>();
        }

        return this.link;
    }

    /**
     * Gets the value of the sym property.
     *
     */
    public String getSym() {
        return sym;
    }

    /**
     * Sets the value of the sym property.
     *
     */
    public void setSym(String value) {
        this.sym = value;
    }

    /**
     * Gets the value of the type property.
     *
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the fix property.
     *
     */
    public String getFix() {
        return fix;
    }

    /**
     * Sets the value of the fix property.
     *
     */
    public void setFix(String value) {
        this.fix = value;
    }

    /**
     * Gets the value of the sat property.
     *
     */
    public int getSat() {
        return sat;
    }

    /**
     * Sets the value of the sat property.
     *
     */
    public void setSat(int value) {
        this.sat = value;
    }

    /**
     * Gets the value of the hdop property.
     *
     */
    public double getHdop() {
        return hdop;
    }

    /**
     * Sets the value of the hdop property.
     *
     */
    public void setHdop(double value) {
        this.hdop = value;
    }

    /**
     * Gets the value of the vdop property.
     *
     */
    public double getVdop() {
        return vdop;
    }

    /**
     * Sets the value of the vdop property.
     *
     */
    public void setVdop(double value) {
        this.vdop = value;
    }

    /**
     * Gets the value of the pdop property.
     *
     */
    public double getPdop() {
        return pdop;
    }

    /**
     * Sets the value of the pdop property.
     *
     */
    public void setPdop(double value) {
        this.pdop = value;
    }

    /**
     * Gets the value of the ageofdgpsdata property.
     *
     */
    public double getAgeofdgpsdata() {
        return ageofdgpsdata;
    }

    /**
     * Sets the value of the ageofdgpsdata property.
     *
     */
    public void setAgeofdgpsdata(double value) {
        this.ageofdgpsdata = value;
    }

    /**
     * Gets the value of the dgpsid property.
     *
     */
    public int getDgpsid() {
        return dgpsid;
    }

    /**
     * Sets the value of the dgpsid property.
     *
     */
    public void setDgpsid(int value) {
        this.dgpsid = value;
    }

    /**
     * Gets the value of the extensions property.
     *
     */
    public ExtensionsType getExtensions() {
        return extensions;
    }

    /**
     * Sets the value of the extensions property.
     *
     */
    public void setExtensions(ExtensionsType value) {
        this.extensions = value;
    }

    /**
     * Gets the value of the lat property.
     *
     */
    public double getLat() {
        return lat;
    }

    /**
     * Sets the value of the lat property.
     *
     */
    public void setLat(double value) {
        this.lat = value;
    }

    /**
     * Gets the value of the lon property.
     *
     */
    public double getLon() {
        return lon;
    }

    /**
     * Sets the value of the lon property.
     *
     */
    public void setLon(double value) {
        this.lon = value;
    }
}