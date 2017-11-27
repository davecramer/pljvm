#------------------------------------------------------------------------------
#
#  
# 
# 

# Global build Directories

PLCONTAINER_DIR = ../src


override CFLAGS += $(CUSTOMFLAGS) -I$(PLCONTAINER_DIR)/ -DCOMM_STANDALONE -Wall -Wextra 

common_src = $(shell find $(PLCONTAINER_DIR)/common -name "*.c")
common_objs = $(foreach src,$(common_src),$(subst .c,.o,$(src)))
test_src = $(shell find . -name "*.c")
test_objs = $(foreach src,$(test_src),$(subst .c,.o,$(src)))

.PHONY: default
default: clean all

.PHONY: clean_common
clean_common:
	rm -f $(common_objs)

%.o: %.c
	$(CC)  $(CFLAGS) -c -o $@ $^

main: $(test_objs) $(common_objs)
	LIBRARY_PATH=$(LD_LIBRARY_PATH) $(CC) -o $@ $^ $(LIBS)

.PHONY: debug
debug: CUSTOMFLAGS = -D_DEBUG_CLIENT -g -O0
debug: main

.PHONY: all
all: CUSTOMFLAGS = -O3
all: main

clean: clean_common
	rm -f *.o
