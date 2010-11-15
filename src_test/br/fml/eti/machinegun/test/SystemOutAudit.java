package br.fml.eti.machinegun.test;

import br.fml.eti.behavior.BuildingException;
import br.fml.eti.machinegun.WrongCapsuleException;
import br.fml.eti.machinegun.auditorship.ArmyAudit;

public class SystemOutAudit implements ArmyAudit {
    @Override
    public void rearSoldierStartsHisJob(long jobId, String soldierName) {
        System.out.println(jobId + ": " + soldierName + ": starts his job");
    }

    @Override
    public void rearSoldierFinishesHisJob(long jobId, String soldierName,
                                          boolean success,
                                          BuildingException exception,
                                          String message) {

        System.out.println(jobId + ": " + soldierName + ": finishes his job: "
                + success + ", exception: " + exception + ", message: " + message);
    }

    @Override
    public void updateBattalionSize(int newSize, int maxSize) {
        System.out.println(newSize + " of " + maxSize + ": "
                + (newSize * 100 / maxSize) + "%");
    }

    @Override
    public void frontLineSoldierIsReady(String soldierName) {
        System.out.println(soldierName + " is ready!");
    }

    @Override
    public void rearSoldierIsReady(String soldierName) {
        System.out.println(soldierName + " is ready!");
    }

    @Override
    public void frontLineSoldierStartsHisJob(long jobId, String soldierName) {
        System.out.println(jobId + ": " + soldierName + ": starts his job");
    }

    @Override
    public void frontLineSoldierFinishesHisJob(long jobId, String soldierName) {
        System.out.println(jobId + ": " + soldierName + ": finishes his job!");
    }

    @Override
    public void frontLineSoldierDied(String soldierName) {
        System.out.println(soldierName + " died!");
    }

    @Override
    public void rearSoldierDied(String soldierName) {
        System.out.println(soldierName + " died!");
    }

    @Override
    public void errorOnBulletCapsule(WrongCapsuleException e) {
        System.out.println("Error: " + e);
        e.printStackTrace();
    }
}
