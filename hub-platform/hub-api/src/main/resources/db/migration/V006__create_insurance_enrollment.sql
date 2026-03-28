CREATE TABLE IF NOT EXISTS insurance_plan (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL REFERENCES hub_case(id) ON DELETE CASCADE,
  plan_sequence VARCHAR(10) NOT NULL DEFAULT 'Primary' CHECK (plan_sequence IN ('Primary','Secondary')),
  insurance_type_code VARCHAR(30) NOT NULL CHECK (insurance_type_code IN ('COMMERCIAL','MARKETPLACE','MEDICARE_A_B','MEDICARE_D','MEDICARE_ADVANTAGE','MEDICAID','TRICARE','VA','UNINSURED','OTHER_GOVERNMENT','UNKNOWN')),
  payer_name VARCHAR(200),
  plan_name VARCHAR(200),
  member_id VARCHAR(100),
  group_id VARCHAR(100),
  rx_bin VARCHAR(20),
  rx_pcn VARCHAR(20),
  effective_date DATE,
  term_date DATE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS benefits_verification (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL REFERENCES hub_case(id) ON DELETE CASCADE,
  status VARCHAR(20) NOT NULL DEFAULT 'Pending' CHECK (status IN ('Pending','InProgress','Complete','Failed')),
  benefit_type VARCHAR(20) CHECK (benefit_type IN ('Pharmacy','Medical','Both')),
  coverage_confirmed BOOLEAN,
  pa_required_per_bv BOOLEAN,
  patient_copay_amt DECIMAL(10,2),
  deductible_met BOOLEAN,
  oop_max_met BOOLEAN,
  formulary_tier INTEGER,
  coverage_notes TEXT,
  verified_by UUID REFERENCES hub_user(id),
  verified_date DATE,
  reverification_due_date DATE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS enrollment_record (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL UNIQUE REFERENCES hub_case(id) ON DELETE CASCADE,
  source VARCHAR(10) NOT NULL CHECK (source IN ('Portal','eRX')),
  received_at TIMESTAMPTZ NOT NULL,
  portal_submission_id VARCHAR(100),
  portal_submitted_by VARCHAR(200),
  erx_transaction_id VARCHAR(100),
  erx_prescriber_npi VARCHAR(10),
  erx_drug_name VARCHAR(200),
  erx_quantity DECIMAL(10,2),
  erx_days_supply INTEGER,
  erx_raw_payload JSONB,
  mi_required BOOLEAN DEFAULT false,
  mi_missing_fields JSONB,
  mi_triggered_at TIMESTAMPTZ,
  mi_resolved_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
