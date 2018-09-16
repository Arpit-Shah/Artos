# Setup Meaven Project and Deploy (OpenSource Project)

Reference1 : [Central Server Provider (Sonatype)](http://central.sonatype.org/pages/apache-maven.html)
Reference2 : [Working with pgp Signature](http://central.sonatype.org/pages/working-with-pgp-signatures.html)
Reference3 : [Maven](https://maven.apache.org/install.html)
Reference4 : [Nexus Repository Manager](https://maven-us.nuxeo.org/nexus/#welcome)

## Pre-requisite

### Github OpenSource Project
* Create [Github](https://github.com/) account.
* Create Github opensource project.

### Creating account in Sonatype
* Register to central server providers website. [Sonatype](https://issues.sonatype.org/secure/Signup.jspa)
> Ensure same email as Github project is used to create an account in central repository (sonatype).
* Login to sonatype account.
* Create new JIRA ticket with new project selected.
* Within JIRA ticket specify following
	* If user owns a domain name then request repository created with domain name.
	> Example : `com.artos`
	> Note : You may need to provide a proof of owning domain name
	* If user does not own domain name then request to create repository for your Github project URL.
	> Example : `com.github.projectname`

* Keep eye on JIRA comments and wait for couple of days before repository is created.

## Set up
### Install and setup Maven
* Download [Maven](https://maven.apache.org/) binary zip.
* Unzip binary to suitable location in PC.
* Set Maven bin location to Windows Environment variables "Path".
> Example : `C:\Maven\apache-maven-3.5.3\bin`
> Note : PC may require restart before Path variables are recognised.
* Launch command line and type `mvn -v`.
* If you see version is printed then maven is setup correctly

### Install and setup GpG
* Download and install [gpg](http://www.gnupg.org/download/) for Windows.
* Run `gpg --version` command
* If you see version is printed then gpg is setup correctly

### Generating key using GPG and deploying public key to server
* Run `gpg --gen-key` to generate key pair. Provide appropriate information and pass phrase when asked.
* Once key pair is generated, run `gpg --list-keys` to list public key
> It reports something like this
> 	C:/Users/arpit/AppData/Roaming/gnupg/pubring.kbx
> 	pub   rsa2048 2018-05-01 [SC] `[expires: 2020-04-30]`
>           197DE90999EB610647952D7BAD3B45A81B83CAAB
> 	uid           [ultimate] Ash Johnes <ash.johnes@gmail.com>
> 	sub   rsa2048 2018-05-01 [E] `[expires: 2020-04-30]`

* Deploy generated public key to remote keyserver using following command:
`gpg --keyserver hkp://pool.sks-keyservers.net --send-keys 197DE90999EB610647952D7BAD3B45A81B83CAAB`
> Note : Key should be same as your public key.
* Run `gpg --list-secret-keys` to list private keys.

# Creating Maven Project
* Create Java Maven Project.
* Add appropriate pom.xml file in root of project which meets all requirement.
	- Provide all dependancy.
	- Provide Organisation Info.
	- Provide licence Info.
	- Provide pulgin for compile, gpg, javadoc, sonatype-staging, src generation.
* create `settings.xml` file which has sonatype account username and password. Store settings.xml file in location where Maven looks for it. If you enable debug log, you will see where settings.xml is being read from (`C:\Users\user\.m2\settings.xml`)
> Read more here : [Maven - settings.xml](https://maven.apache.org/settings.html)
* Perform Maven clean
* Perform Maven install
* perform Maven deploy
* Build should be successful if maven and gpg is setup correctly

