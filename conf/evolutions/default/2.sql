# categories initial content

# --- !Ups

insert into category (name, slug) values
('Java', 'java'),
('C / C++ / C#', 'c'),
('Objective C', 'objc'),
('JavaScript', 'javascript'),
('HTML & CSS', 'html-css'),
('PHP', 'php'),
('Python', 'python'),
('Ruby', 'ruby'),
('Базы данных', 'database'),
('Прочее', 'other');

# --- !Downs

delete from category where slug='java';
delete from category where slug='c';
delete from category where slug='objc';
delete from category where slug='javascript';
delete from category where slug='html-css';
delete from category where slug='php';
delete from category where slug='python';
delete from category where slug='ruby';
delete from category where slug='database';
delete from category where slug='other';

# pages initial content

# --- !Ups

insert into page (slug, title, content) values ('about', 'About', '');

# --- !Downs

delete from page where slug='about';
