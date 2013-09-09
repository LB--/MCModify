#include <iostream>
#include <fstream>
#include <sstream>
#include <limits>
#include <exception>

#include "NamedBinaryTag.hpp"

#include <boost/iostreams/filtering_streambuf.hpp>
#include <boost/iostreams/filter/gzip.hpp>
#include <boost/iostreams/copy.hpp>

int main(int nargs, char const *const *args)
{
	if(nargs != 2) return 1;

	std::stringstream decompressed;
	{
		std::ifstream compressed (args[1], std::ios::in|std::ios::binary);
		if(!compressed) return 2;
		boost::iostreams::filtering_istreambuf fis;
		fis.push(boost::iostreams::gzip_decompressor());
		fis.push(compressed);
		boost::iostreams::copy(fis, decompressed);
		decompressed.seekg(std::ios::beg);
	}

	std::cout << "Reading " << args[1] << std::endl;
	auto nbt = NBT::Tag::read(decompressed);

	std::cout << "Writing " << args[1] << ".out" << std::endl;
	{
		std::stringstream decompout;
		nbt->write(decompout);
		std::ofstream out (args[1]+std::string(".out"), std::ios::out|std::ios::binary);
		boost::iostreams::filtering_ostreambuf fos;
		fos.push(boost::iostreams::gzip_compressor());
		fos.push(out);
		boost::iostreams::copy(decompout, fos);
	}
	std::cout << "Done" << std::endl;

	std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
}
