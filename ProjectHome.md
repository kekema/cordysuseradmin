Utility to manage users in Cordys, enabling better overview and faster interaction. Features include:
  * create, clone, update, delete users
  * role/task/team assignments in one shot
  * multiple user management
  * import/export users
  * synchronize users from external directory (like Active Directory)

A 3-layered Java API is included (UI, domain, Cordys layers), hosted in WS-AppServer.

Supported Cordys versions: Cordys 4.2 / Cordys 4.3 / OpenText Cordys 10.5

Installation instructions:
  * Deploy the CAP
    * For Cordys 4.2: [CAP](http://cordysuseradmin.googlecode.com/svn/tags/release_v1.0/build/EkemaIT%20UserAdmin%201.0.8.cap)
    * For Cordys 4.3: [CAP](http://cordysuseradmin.googlecode.com/svn/tags/release_v1.1/build/EkemaIT%20UserAdmin%201.1.0.cap)
    * For OpenText Cordys 10.5: [CAP](http://cordysuseradmin.googlecode.com/svn/trunk/build/EkemaIT%20UserAdmin%201.2.3.cap)
  * Attach the 2 UserAdmin interfaces to a WS-AppServer (in org or in system)
  * jre classpath configure <Cordys Instance>\com-cordys-coe\useradmin\java\UserAdmin.jar;
  * Assign UserAdmin role (Cordys Administrator role also required)
  * Start User Admin from taskbar
  * (for sync users from external directory, configure a Generic LDAP SG)
<br>
Sources:<br>
<ul><li>For Cordys 4.2 : tags/release_v1.0<br>
</li><li>For Cordys 4.3 : tags/release_v1.1<br>
</li><li>For OpenText Cordys 10.5 : trunk<br>
<br>
<img src='http://cordysuseradmin.googlecode.com/svn/trunk/images/Maintain%20Users.jpg' />
<br><br>
<img src='http://cordysuseradmin.googlecode.com/svn/trunk/images/User%20Assignments.jpg' />