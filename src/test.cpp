#include <iostream>
#include <fstream>
#include <sstream>
#include <limits>

#include "NamedBinaryTag.hpp"

#include <boost/iostreams/filtering_streambuf.hpp>
#include <boost/iostreams/filter/gzip.hpp>
#include <boost/iostreams/copy.hpp>

int main(int nargs, char const *const *args)
{
	if(nargs != 2) return 1;

	std::ifstream compressed (args[1], std::ios::in|std::ios::binary);
	if(!compressed) return 2;
	std::stringstream decompressed;
	boost::iostreams::filtering_istreambuf fis;
	fis.push(boost::iostreams::gzip_decompressor());
	fis.push(compressed);
	boost::iostreams::copy(fis, decompressed);
	decompressed.seekg(std::ios::beg);
	auto nbt = NBT::Tag::read(decompressed);

	std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
}
