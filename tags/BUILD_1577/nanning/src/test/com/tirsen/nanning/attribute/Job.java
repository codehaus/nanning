package com.tirsen.nanning.attribute;

/**
 * @transaction required
 */
public class Job {
    /**
     * @transient true
     */
    private String description;

    private String boss;

    public void fireAllEmployees() {
    }

    /**
     * @nanning great
     * @param employee
     */
    public void hireEmployee(String reason, Employee employee) {
    }

}
