package com.hubaccess.domain.program.dto;

import lombok.Data;

@Data
public class UpdateProgramConfigRequest {
    private Boolean paRequired;
    private Boolean adherenceProgramEnabled;
    private Boolean remsTrackingEnabled;
    private String enrollmentSources;
    private Boolean miRequiredForErx;
    private String miRequiredFields;
    private Boolean copayEnabled;
    private Boolean papEnabled;
    private Boolean bridgeEnabled;
    private Boolean copayIncomeLimitEnabled;
    private Integer copayIncomeLimitFplPct;
    private Integer copayMonthlyCapUsd;
    private Integer copayAnnualCapUsd;
    private Integer copayEnrollmentMonths;
    private Integer copayMinAge;
    private Integer papFplThresholdPct;
    private Boolean papAllowCommercialInsured;
    private Boolean papProofOfIncomeRequired;
    private Boolean papAttestationOnly;
    private Integer papSupplyDays;
    private Integer papEnrollmentMonths;
    private Integer papMinAge;
    private Boolean bridgeTriggerPaPending;
    private Boolean bridgeTriggerCoverageLapse;
    private Boolean bridgeTriggerNewEnrollment;
    private Integer bridgeSupplyDays;
    private Integer bridgeMaxEpisodesPerYear;
    private Boolean bridgeNewPatientOnly;
    private Boolean bridgeIncomeLimitEnabled;
    private Integer bridgeIncomeLimitFplPct;
    private Integer paSlaSumbitDays;
    private Integer paSlaFollowupDays;
    private Integer paAppealWindowDays;
    private Boolean paAutoEscalateOnBreach;
    private Integer consentUrlExpiryDays;
}
