INSERT INTO role (name, description, permissions) VALUES
  ('HubAdmin',          'Full system access',                         '["*"]'),
  ('Supervisor',        'All case access for assigned programs',       '["case:*","task:*","outreach:*","report:read","user:assign"]'),
  ('CaseManager',       'Case access for assigned programs only',      '["case:read","case:write","case:close","task:read","task:write","task:complete","outreach:send","pa:submit","fa:evaluate","report:read"]'),
  ('ManufacturerViewer','Read-only access to own manufacturer programs','["case:read","report:read"]');
