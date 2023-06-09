
create table if not exists settings (
    settings_id bigint auto_increment not null primary key,
    deadline_alarm bit(1),
    deadline_alarm_term int(11),
    notification_perm varchar(255)
);


create table if not exists member (
    member_id bigint auto_increment not null primary key,
    email varchar(255),
    nickname varchar(255),
    password varchar(255),
    settings_id bigint not null,
    foreign key (settings_id) references settings(settings_id)
);

create table if not exists auth (
    auth_id bigint auto_increment not null primary key,
    key_sha256 varchar(255),
    member_id bigint not null,
    foreign key (member_id) references member(member_id)
);

create table if not exists plan (
    dtype varchar(31),
    plan_id bigint auto_increment not null primary key,
    checked_tododate_cnt int(11),
    emphasis bit(1),
    plan_status varchar(255),
    start_date date,
    title varchar(255),
    unchecked_tododate_cnt int(11),
    member_id bigint not null,
    foreign key (member_id) references member(member_id)
);

create table if not exists plan_regular (
    plan_id bigint auto_increment not null primary key
);

create table if not exists plan_term (
    end_date date,
    end_time time,
    plan_id bigint auto_increment not null primary key
);


create table if not exists todo (
    todo_id bigint auto_increment not null primary key,
    rep_option int(11),
    title varchar(255),
    member_id bigint not null,
    plan_id bigint not null,
    foreign key (member_id) references member(member_id),
    foreign key (plan_id) references plan(plan_id)
);

create table if not exists todo_date (
    dtype varchar(31),
    todo_date_id bigint auto_increment not null primary key,
    date_key date,
    todo_status varchar(255)
);

create table if not exists todo_date_rep (
    title varchar(255),
    todo_date_id bigint auto_increment not null primary key,
    todo_id bigint not null,
    foreign key (todo_id) references todo(todo_id)
);


create table if not exists todo_date_daily (
    title varchar(255),
    todo_date_id bigint auto_increment not null primary key,
    plan_id bigint not null,
    foreign key (plan_id) references plan(plan_id)
);

create table if not exists todo_date_comment (
    comment_id bigint auto_increment not null primary key,
    comment varchar(255),
    todo_date_id bigint not null,
    foreign key (todo_date_id) references todo_date(todo_date_id)
);

create table if not exists todo_rep_value (
    todo_id bigint not null,
    rep_value varchar(255),
    primary key (todo_id, rep_value),
    foreign key (todo_id) references todo(todo_id)
);