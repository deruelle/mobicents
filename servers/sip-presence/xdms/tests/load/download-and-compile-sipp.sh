# Compile sipp
wget -m -nv -nd 'http://downloads.sourceforge.net/sipp/sipp.3.1.src.tar.gz'
tar -xzf sipp.3.1.src.tar.gz
rm -fr sipp.3.1.src.tar.gz
cd sipp.svn

# Fix for RedHat
sed 's|#include <sys/socket.h>|&\n#include <limits.h>|' < scenario.hpp > my-scenario.hpp
mv -f my-scenario.hpp scenario.hpp

make > /dev/null

mv -f sipp ..
cd ..
rm -fr  sipp.svn
