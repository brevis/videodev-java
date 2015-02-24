# category initial content

# --- !Ups

insert into category (name, slug) values
('Java', 'java'),
('JavaScript', 'javascript'),
('HTML & CSS', 'html-css'),
('PHP', 'php'),
('Python', 'python'),
('Ruby', 'ruby'),
('Базы данных', 'database'),
('Прочее', 'other');

# --- !Downs

delete from category where slug='java';
delete from category where slug='javascript';
delete from category where slug='html-css';
delete from category where slug='php';
delete from category where slug='python';
delete from category where slug='ruby';
delete from category where slug='database';
delete from category where slug='other';