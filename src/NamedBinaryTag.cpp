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
		{ List<End>::ID, [](Name_t const &name, std::istream &is) -> std::unique_ptr<Tag>
			{
				ID_t Id {Byte("", is).v};
				switch(Id)
				{
				case Byte::ID:      return new List<Byte>(name, is);
				case Short::ID:     return new List<Short>(name, is);
				case Int::ID:       return new List<Int>(name, is);
				case Long::ID:      return new List<Long>(name, is);
				case Float::ID:     return new List<Float>(name, is);
				case Double::ID:    return new List<Double>(name, is);
				case ByteArray::ID: return new List<BteArray>(name, is);
				case String::ID:    return new List<String>(name, is);
				case List<End>::ID: return ;
				case Compound::ID:  return new List<Compound>(name, is);
				case IntArray::ID:  return new List<IntArray>(name, is);
				}
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
