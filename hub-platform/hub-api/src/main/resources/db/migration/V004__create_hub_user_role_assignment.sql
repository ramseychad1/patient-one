CREATE TABLE IF NOT EXISTS hub_user (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(200) NOT NULL UNIQUE,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  password_hash VARCHAR(500) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'Active' CHECK (status IN ('Active','Inactive','Locked')),
  mfa_enabled BOOLEAN DEFAULT false,
  mfa_secret VARCHAR(200),
  manufacturer_id UUID REFERENCES manufacturer(id),
  last_login_at TIMESTAMPTZ,
  failed_login_count INTEGER DEFAULT 0,
  locked_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS role (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(100) NOT NULL UNIQUE,
  description TEXT,
  permissions JSONB NOT NULL DEFAULT '[]',
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_role (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES hub_user(id) ON DELETE CASCADE,
  role_id UUID NOT NULL REFERENCES role(id),
  granted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  granted_by UUID REFERENCES hub_user(id),
  UNIQUE(user_id, role_id)
);

CREATE TABLE IF NOT EXISTS user_program_assignment (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES hub_user(id) ON DELETE CASCADE,
  program_id UUID NOT NULL REFERENCES program(id) ON DELETE CASCADE,
  access_level VARCHAR(20) NOT NULL DEFAULT 'ReadWrite' CHECK (access_level IN ('ReadOnly','ReadWrite','Supervisor')),
  assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  assigned_by UUID REFERENCES hub_user(id),
  expires_at TIMESTAMPTZ,
  UNIQUE(user_id, program_id)
);
