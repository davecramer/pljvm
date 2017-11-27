## PL/Container

This is an implementation of trusted language execution engine capable of
bringing up Docker containers to isolate executors from the host OS, i.e.
implement sandboxing.

### Requirements

1. PL/Container runs on CentOS/RHEL 7.x as this is a prerequisite for Docker
1. PL/Container requires Docker v1.10+ as it is built with Docker API v1.22
You can find more information in the user guide

### Building Environment

```shell
git clone https://github.com/greenplum-db/plcontainer.git
cd plcontainer/vagrant/centos
vagrant up
```

That's it, Greenplum is built and installed, database is up and running,
PL/Container code is available in `/plcontainer` directory inside of VM

### Building PL/Container Language

You can build PL/Container in a following way:

1. Login to Vagrant: `vagrant ssh`
1. Go to the PL/Container directory: `cd /plcontainer`
1. Make and install it: `make clean && make && make install`

Database restart is not required to catch up new container execution library,
you can simply connect to the database and all the calls you will make to
plcontainer language would be held by new binaries you just built

### Building Containers

To build containers you have to:

1. Login to Vagrant: `vagrant ssh`
1. Go to the PL/Container directory: `cd /plcontainer`
1. Make it: `make containers`

### Configuring PL/Container

You can start with the default configuration. To apply it do the following:
1. Login to Vagrant: `vagrant ssh`
1. Reset the configuration: `plcontainer-config --reset`

### Running the tests

1. Login to Vagrant: `vagrant ssh`
1. Go to the PL/Container test directory: `cd /plcontainer`
1. Make it: `make installcheck`

### Design

The idea of PL/Container is to use containers to run user defined functions. The
current implementation assume the PL function definition to have the following
structure:

```sql
CREATE FUNCTION dummyPython() RETURNS text AS $$
# container: plc_python
return 'hello from Python'
$$ LANGUAGE plcontainer;
```

There are a couple of things that are interesting here:

1. The function definition starts with the line `# container: plc_python` which
defines the name of container that will be used for running this function. To
check the list of containers defined in the system you can run the command
`plcontainer-config --show`. Each container is mapped to a single docker image,
you can list the ones available in your system with command `docker images`

1. The implementation assumes Docker container exposes some port, i.e. the
container is started by an API call similar to running `docker run -d -P <image>`
to publish the exposed port to a random port on the host. For an example of how
to create a compatible docker image see `Dockerfile.R` and `Dockerfile.python`

1. The `LANGUAGE` argument to Greenplum is `plcontainer`

1. At the moment two languages are supported for PL/Container: R and Python.
Here is the list of supported containers:
  * plc_python - Docker image `pivotaldata/plcontainer_python:0.1.0-1` with
Python
  * plc_anaconda - Docker image `pivotaldata/plcontainer_anaconda:0.1.0-1`
implements Anaconda Python
  * plc_r - Docker image `pivotaldata/plcontainer_r:0.1.0-1` with R
  * plc_python_shared - Docker image `pivotaldata/plcontainer_python_shared:0.1.0-1`
with Python sharing the Greenplum-provided Python execution environment
  * plc_r_shared - Docker image `pivotaldata/plcontainer_r_shared:0.1.0-1` with
R sharing the Greenplum-provided R execution environment

For example, to define a function that uses a container that runs the `R`
interpreter, simply make the following definition:
```sql
CREATE FUNCTION dummyR() RETURNS text AS $$
# container: plc_r
return(log10(100))
$$ LANGUAGE plcontainer;
```
