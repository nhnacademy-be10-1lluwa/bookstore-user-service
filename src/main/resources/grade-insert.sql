INSERT INTO grade (grade_name, priority, point_rate, min_amount, max_amount)
VALUES
    ('BASIC',    4, 0.01, 0,      100000),
    ('GOLD',     3, 0.02, 100000, 200000),
    ('ROYAL',    2, 0.025, 200000, 300000),
    ('PLATINUM', 1, 0.03, 300000, NULL);
