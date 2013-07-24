#include "NamedBinaryTag.hpp"

namespace NBT
{
	Tag::End const Tag::End::END;
	std::map<Tag::ID_t, std::function<std::unique_ptr<Tag> (Tag::Name_t const &name, std::istream &)>> const Tag::readers
	{
		{ Byte::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return std::unique_ptr<Tag>(new Byte(name, is));
			}
		},
		{ Short::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return std::unique_ptr<Tag>(new Short(name, is));
			}
		},
		{ Int::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return std::unique_ptr<Tag>(new Int(name, is));
			}
		},
		{ Long::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return std::unique_ptr<Tag>(new Long(name, is));
			}
		},
		{ Float::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return std::unique_ptr<Tag>(new Float(name, is));
			}
		},
		{ Double::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return std::unique_ptr<Tag>(new Double(name, is));
			}
		},
		{ ByteArray::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return std::unique_ptr<Tag>(new ByteArray(name, is));
			}
		},
		{ String::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return std::unique_ptr<Tag>(new String(name, is));
			}
		},
		{ List::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				ID_t type {Byte(L"", is).v};
				return std::unique_ptr<Tag>(new List(name, type, is));
			}
		},
		{ Compound::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return std::unique_ptr<Tag>(new Compound(name, is));
			}
		},
		{ IntArray::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				return std::unique_ptr<Tag>(new IntArray(name, is));
			}
		}
	};
}
