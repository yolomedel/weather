to start the Frontend: in Console:

cd in folder absolute-accretion

    npm install

run:    

    npm run dev

to use the database:

install docker desktop
install a mysql database in a docker container

    $ docker run --name weatherbase -e MYSQL_ROOT_PASSWORD=Namenlos12 -d mysql

connect the database to the server
add the collums Role_User/Role_Admin/Role_Moderator to the role dataset

    INSERT INTO roles (role_name) VALUES 
    ('Role_User'),
    ('Role_Moderator'),
    ('Role_Admin');

to start backend:

Start docker Container with the mysql database
in folder weather run:

    ./gradlew bootRun


