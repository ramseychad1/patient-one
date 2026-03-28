CREATE TABLE IF NOT EXISTS case_task (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL REFERENCES hub_case(id) ON DELETE CASCADE,
  task_type VARCHAR(30) NOT NULL CHECK (task_type IN ('ConsentOutreach','MIOutreach','BIVerification','PASubmission','PAFollowup','FAEvaluation','BridgeArrange','PharmacyOrder','CheckIn','Escalation','Other')),
  title VARCHAR(300) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'Open' CHECK (status IN ('Open','InProgress','Completed','Cancelled')),
  priority VARCHAR(10) NOT NULL DEFAULT 'Normal' CHECK (priority IN ('Low','Normal','High','Urgent')),
  due_date DATE,
  assigned_to UUID REFERENCES hub_user(id),
  completed_at TIMESTAMPTZ,
  completed_by UUID REFERENCES hub_user(id),
  notes TEXT,
  auto_generated BOOLEAN DEFAULT false,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by UUID REFERENCES hub_user(id)
);

CREATE TABLE IF NOT EXISTS interaction (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL REFERENCES hub_case(id) ON DELETE CASCADE,
  interaction_type VARCHAR(30) NOT NULL CHECK (interaction_type IN ('InboundCall','OutboundCall','SMS','Email','Note','DocumentReceived','DocumentSent','PrescriptionReceived','PAUpdate','ShipmentUpdate')),
  direction VARCHAR(10) CHECK (direction IN ('Inbound','Outbound','Internal')),
  channel VARCHAR(20) CHECK (channel IN ('Phone','SMS','Email','Portal','System')),
  summary VARCHAR(500) NOT NULL,
  notes TEXT,
  document_reference VARCHAR(500),
  performed_by UUID REFERENCES hub_user(id),
  interaction_at TIMESTAMPTZ NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS case_status_history (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL REFERENCES hub_case(id),
  from_stage VARCHAR(30),
  to_stage VARCHAR(30) NOT NULL,
  from_status VARCHAR(50),
  to_status VARCHAR(50) NOT NULL,
  changed_by UUID REFERENCES hub_user(id),
  changed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  change_reason VARCHAR(500)
);

CREATE RULE no_update_case_status_history AS ON UPDATE TO case_status_history DO INSTEAD NOTHING;
CREATE RULE no_delete_case_status_history AS ON DELETE TO case_status_history DO INSTEAD NOTHING;
