INSERT INTO festival_area (id, name, description, location, type) VALUES
(1, 'Main Stage', 'The main performance stage', 'North Field', 'Stage'),
(2, 'Food Village', 'Street food and restaurants', 'South Field', 'Food'),
(3, 'Craft Beer Bar', 'Local craft beers', 'East Field', 'Bar'),
(4, 'First Aid Point', 'Medical assistance', 'Central', 'Medical');

INSERT INTO crowd_report (id, area_id, level, note, submitted_at) VALUES
(1, 1, 'MEDIUM', 'Getting busy', '2026-05-07T10:00:00'),
(2, 1, 'FULL', 'Packed out', '2026-05-07T11:00:00'),
(3, 2, 'LOW', 'Quiet so far', '2026-05-07T10:30:00'),
(4, 3, 'MEDIUM', 'Steady flow', '2026-05-07T10:45:00');

INSERT INTO crowd_alert (id, area_id, message, status, created_at) VALUES
(1, 1, 'Main Stage is at full capacity', 'ACTIVE', '2026-05-07T11:00:00'),
(2, 4, 'First Aid Point is at full capacity', 'RESOLVED', '2026-05-07T09:00:00');

ALTER TABLE festival_area ALTER COLUMN id RESTART WITH 100;
ALTER TABLE crowd_report ALTER COLUMN id RESTART WITH 100;
ALTER TABLE crowd_alert ALTER COLUMN id RESTART WITH 100;
