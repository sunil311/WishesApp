package com.impetus.mailsender.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EMPLOYEE_DETAILS")
public class Employee implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "TEMPLATE_NAME")
    private String templateName;
    @Column(name = "NAME")
    private String NAME;
    @Column(name = "IMG_URL")
    private String IMGURL;
    @Column(name = "SUBJECT")
    private String SUBJECT;
    @Column(name = "EMAIL")
    private String EMAIL;
    @Column(name = "EMP_ID")
    private String empId;
    @Column(name = "CLIENT")
    private String client;
    @Column(name = "PROJECT")
    private String project;
    @Column(name = "B_DAY")
    private Date bDay;
    @Column(name = "ANNIVERSARY")
    private Date anniversary;
    @Column(name = "DOJ")
    private Date doj;
    @Column(name = "LOCATION")
    private String location;

    public Employee copy() {
        Employee employee = new Employee();
        employee.setAnniversary(this.anniversary);
        employee.setbDay(this.bDay);
        employee.setDoj(this.doj);
        employee.setClient(this.client);
        employee.setProject(this.project);
        employee.setTemplateName(this.templateName);
        employee.setNAME(this.NAME);
        employee.setIMGURL(this.IMGURL);
        employee.setSUBJECT(this.SUBJECT);
        employee.setEMAIL(this.EMAIL);
        employee.setEmpId(this.empId);
        employee.setId(this.id);
        return employee;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String nAME) {
        NAME = nAME;
    }

    public String getIMGURL() {
        return IMGURL;
    }

    public void setIMGURL(String iMGURL) {
        IMGURL = iMGURL;
    }

    public String getSUBJECT() {
        return SUBJECT;
    }

    public void setSUBJECT(String sUBJECT) {
        SUBJECT = sUBJECT;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String eMAIL) {
        EMAIL = eMAIL;
    }

    @Override
    public String toString() {
        return "Employee [id=" + id + ", templateName=" + templateName + ", NAME=" + NAME + ", IMGURL=" + IMGURL + ", SUBJECT=" + SUBJECT + ", EMAIL="
                + EMAIL + ", empId=" + empId + ", client=" + client + ", project=" + project + ", bDay=" + bDay + ", anniversary=" + anniversary
                + ", doj=" + doj + ", locations=" + location + "]";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Date getbDay() {
        return bDay;
    }

    public void setbDay(Date bDay) {
        this.bDay = bDay;
    }

    public Date getAnniversary() {
        return anniversary;
    }

    public void setAnniversary(Date anniversary) {
        this.anniversary = anniversary;
    }

    public Date getDoj() {
        return doj;
    }

    public void setDoj(Date doj) {
        this.doj = doj;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
