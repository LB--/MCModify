#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <limits>
#include <exception>

#include <NamedBinaryTag.hpp>

#include <boost/iostreams/filtering_streambuf.hpp>
#include <boost/iostreams/filter/gzip.hpp>
#include <boost/iostreams/copy.hpp>

std::string const INPUT = "level.in.dat";
std::string const OUTPUT = "level.out.dat";

int main(int nargs, char const *const *args)
{
	std::stringstream decompressed;
	{
		std::ifstream compressed (INPUT, std::ios::in|std::ios::binary);
		if(!compressed) return -1;
		boost::iostreams::filtering_istreambuf fis;
		fis.push(boost::iostreams::gzip_decompressor());
		fis.push(compressed);
		boost::iostreams::copy(fis, decompressed);
		decompressed.seekg(std::ios::beg);
	}

	std::cout << "Reading " << INPUT << std::endl;
	auto nbt = NBT::Tag::read(decompressed);

	std::cout << "Writing " << OUTPUT << std::endl;
	{
		std::stringstream decompout;
		nbt->write(decompout);
		std::ofstream out (OUTPUT, std::ios::out|std::ios::binary);
		boost::iostreams::filtering_ostreambuf fos;
		fos.push(boost::iostreams::gzip_compressor());
		fos.push(out);
		boost::iostreams::copy(decompout, fos);
	}
	std::cout << "Done, press enter to exit" << std::flush;
	std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
}
