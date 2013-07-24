#include "NamedBinaryTag.hpp"

namespace NBT
{
	Tag::End const Tag::End::END;
	std::map<ID_t, std::function<std::unique_ptr<Tag> (Name_t const &name, std::istream &)>> const Tag::readers
	{
		{ Byte::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return new Byte(name, is);
			}
		},
		{ Short::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return new Short(name, is);
			}
		},
		{ Int::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return new Int(name, is);
			}
		},
		{ Long::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return new Long(name, is);
			}
		},
		{ Float::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return new Float(name, is);
			}
		},
		{ Double::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return new Double(name, is);
			}
		},
		{ ByteArray::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return new ByteArray(name, is);
			}
		},
		{ String::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return new String(name, is);
			}
		},
		{ List::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				ID_t type {Byte("", is).v};
				return new List(name, type, is);
			}
		},
		{ Compound::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return new Compound(name, is);
			}
		},
		{ IntArray::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return new IntArray(name, is);
			}
		}
	};
}
