CREATE TABLE IF NOT EXISTS observe(
    user_id int,
    task_id int,
    primary key (user_id, task_id),
    foreign key (user_id) references users(user_id),
    foreign key (task_id) references task(task_id) ON DELETE CASCADE
);
