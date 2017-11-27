#------------------------------------------------------------------------------
#
# 
# Copyright (c) 2016, Pivotal.
#
#------------------------------------------------------------------------------
MODULE_big = pljvm


ifeq ($(CIBUILD),1)
  IMAGE_TAG=devel
else
  IMAGE_TAG=$(PLCONTAINER_VERSION).$(PLCONTAINER_RELEASE)-$(PLCONTAINER_IMAGE_VERSION)
endif

# Directories

SRCDIR = ./src

EXTENSION 	= pljvm
MODULE_big 	= pljvm
REGRESS 	= pljvm
# Files to build
FILES 	= $(shell find $(SRCDIR) -not -path "*client*" -type f -name "*.c")
OBJS 	= $(foreach FILE,$(FILES),$(subst .c,.o,$(FILE)))
DATA	= pljvm--1.0.0.sql 

PG_CONFIG = pg_config
PGXS := $(shell $(PG_CONFIG) --pgxs)
include $(PGXS)


