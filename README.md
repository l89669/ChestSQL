ChestSQL
========
This is a CraftBukkit plugin.

Dependences: ProtocolLib

Minecraft: 1.6.4 - 1.10

This plugin needs MySQL to work correctly.

What can it do:
===============
It can save inventory to database and reopen it in another server.

Support mod items and nbtdata.

Commands:
=========

/chest - to open player's private chest

/chest [name] - to open a public chest named [name]

/chestadmin lock [name]- lock a public chest named [name]

/chestadmin unlock [name] - unlock a public chest named [name]

Permissions:
=========

chestsql.use - to open player's private chest

chestsql.public.[name] - to open a public chest named [name]

chestsql.admin - to use /chestadmin lock and /chestadmin unlock [name]
