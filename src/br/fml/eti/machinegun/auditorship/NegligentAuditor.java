package br.fml.eti.machinegun.auditorship;

import br.fml.eti.behavior.BuildingException;

/**
 * A {@link NegligentAuditor} do nothing. Use it if you think the
 * monitor time is taking too much unnecessary time.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 4:27:30 PM
 */
public class NegligentAuditor implements ArmyAudit {
    @Override
    public void rearSoldierStartsHisJob(long jobId, String soldierName) {

    }

    @Override
    public void rearSoldierFinishesHisJob(long jobId, String soldierName,
                                          boolean success,
                                          BuildingException exception,
                                          String message) {

    }

    @Override
    public void updateBattalionSize(int newSize, int maxSize) {

    }

    @Override
    public void frontLineSoldierIsReady(String soldierName) {

    }

    @Override
    public void rearSoldierIsReady(String soldierName) {

    }

    @Override
    public void frontLineSoldierStartsHisJob(long jobId, String soldierName) {

    }

    @Override
    public void frontLineSoldierFinishesHisJob(long jobId, String soldierName) {

    }

    @Override
    public void frontLineSoldierDied(String soldierName) {

    }
}
