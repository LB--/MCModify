#ifndef MinecraftNamedBinaryTagClasses_HeaderPlusPlus
#define MinecraftNamedBinaryTagClasses_HeaderPlusPlus
#include <string>
#include <cstdint>
#include <vector>
#include <type_traits>
#include <map>
#include <memory>
#include <iostream>
#include <functional>
#include <initializer_list>

namespace NBT
{
	struct Tag
	{
		static std::int32_t const NBT_VERSION = 19133;

		using Name_t = std::wstring;
		Name_t const name;
		using ID_t = std::int8_t;

		Tag() = default;
		Tag(Name_t const &name)
		: name(name)
		{
		}
		Tag(Tag const &) = default;
		Tag(Tag &&) = default;
		Tag &operator=(Tag const &) = default;
		Tag &operator=(Tag &&) = default;
		virtual ~Tag() = default;

		virtual ID_t id() const = 0;
		virtual std::unique_ptr<Tag> clone() const = 0;

		virtual std::ostream &writePayload(std::ostream &) const = 0;
		virtual std::ostream &write(std::ostream &os) const;
		static std::unique_ptr<Tag> read(std::istream &is);

		struct End;
		struct Byte;
		struct Short;
		struct Int;
		struct Long;
		struct Float;
		struct Double;
		struct ByteArray;
		struct String;
		struct List;
		struct Compound;
		struct IntArray;

		static std::map<ID_t, std::function<std::unique_ptr<Tag> (Name_t const &name, std::istream &)>> const readers;
	};
	struct Tag::End : Tag
	{
		static ID_t const ID = 0;

		static End const END;

		virtual ID_t id() const
		{
			return ID;
		}
		virtual std::unique_ptr<Tag> clone() const
		{
			return std::unique_ptr<Tag>();
		}

		virtual std::ostream &writePayload(std::ostream &os) const
		{
			return os;
		}
		virtual std::ostream &write(std::ostream &os) const
		{
			return os.write(reinterpret_cast<char const *>(&ID), sizeof(ID_t));
		}
	private:
		End(...)
		{
		}
	};
	struct Tag::Byte : Tag
	{
		static ID_t const ID = 1;
		using t = std::int8_t;
		t v;

		using Tag::Tag;

		Byte(t v)
		: v(v)
		{
		}
		Byte(Name_t const &name, t v)
		: Tag(name)
		, v(v)
		{
		}

		virtual ID_t id() const
		{
			return ID;
		}
		virtual std::unique_ptr<Tag> clone() const
		{
			return std::unique_ptr<Tag>(new Byte(*this));
		}

		virtual std::ostream &writePayload(std::ostream &os) const
		{
			return os.write(reinterpret_cast<char const *>(&v), sizeof(t));
		}
		Byte(Name_t const &name, std::istream &is)
		: Tag(name)
		{
			is.read(reinterpret_cast<char *>(&v), sizeof(t));
		}
	};
	struct Tag::Short : Tag
	{
		static ID_t const ID = 2;
		using t = std::int16_t;
		t v;

		using Tag::Tag;

		Short(t v)
		: v(v)
		{
		}
		Short(Name_t const &name, t v)
		: Tag(name)
		, v(v)
		{
		}

		virtual ID_t id() const
		{
			return ID;
		}
		virtual std::unique_ptr<Tag> clone() const
		{
			return std::unique_ptr<Tag>(new Short(*this));
		}

		virtual std::ostream &writePayload(std::ostream &os) const
		{
			return os.write(reinterpret_cast<char const *>(&v), sizeof(t));
		}
		Short(Name_t const &name, std::istream &is)
		: Tag(name)
		{
			is.read(reinterpret_cast<char *>(&v), sizeof(t));
		}
	};
	struct Tag::Int : Tag
	{
		static ID_t const ID = 3;
		using t = std::int32_t;
		t v;

		using Tag::Tag;

		Int(t v)
		: v(v)
		{
		}
		Int(Name_t const &name, t v)
		: Tag(name)
		, v(v)
		{
		}

		virtual ID_t id() const
		{
			return ID;
		}
		virtual std::unique_ptr<Tag> clone() const
		{
			return std::unique_ptr<Tag>(new Int(*this));
		}

		virtual std::ostream &writePayload(std::ostream &os) const
		{
			return os.write(reinterpret_cast<char const *>(&v), sizeof(t));
		}
		Int(Name_t const &name, std::istream &is)
		: Tag(name)
		{
			is.read(reinterpret_cast<char *>(&v), sizeof(t));
		}
	};
	struct Tag::Long : Tag
	{
		static ID_t const ID = 4;
		using t = std::int64_t;
		t v;

		using Tag::Tag;

		Long(t v)
		: v(v)
		{
		}
		Long(Name_t const &name, t v)
		: Tag(name)
		, v(v)
		{
		}

		virtual ID_t id() const
		{
			return ID;
		}
		virtual std::unique_ptr<Tag> clone() const
		{
			return std::unique_ptr<Tag>(new Long(*this));
		}

		virtual std::ostream &writePayload(std::ostream &os) const
		{
			return os.write(reinterpret_cast<char const *>(&v), sizeof(t));
		}
		Long(Name_t const &name, std::istream &is)
		: Tag(name)
		{
			is.read(reinterpret_cast<char *>(&v), sizeof(t));
		}
	};
	struct Tag::Float : Tag
	{
		static ID_t const ID = 5;
		using t = float;
		t v;

		using Tag::Tag;

		Float(t v)
		: v(v)
		{
		}
		Float(Name_t const &name, t v)
		: Tag(name)
		, v(v)
		{
		}

		virtual ID_t id() const
		{
			return ID;
		}
		virtual std::unique_ptr<Tag> clone() const
		{
			return std::unique_ptr<Tag>(new Float(*this));
		}

		virtual std::ostream &writePayload(std::ostream &os) const
		{
			return os.write(reinterpret_cast<char const *>(&v), sizeof(t));
		}
		Float(Name_t const &name, std::istream &is)
		: Tag(name)
		{
			is.read(reinterpret_cast<char *>(&v), sizeof(t));
		}
	};
	struct Tag::Double : Tag
	{
		static ID_t const ID = 6;
		using t = double;
		t v;

		using Tag::Tag;

		Double(t v)
		: v(v)
		{
		}
		Double(Name_t const &name, t v)
		: Tag(name)
		, v(v)
		{
		}

		virtual ID_t id() const
		{
			return ID;
		}
		virtual std::unique_ptr<Tag> clone() const
		{
			return std::unique_ptr<Tag>(new Double(*this));
		}

		virtual std::ostream &writePayload(std::ostream &os) const
		{
			return os.write(reinterpret_cast<char const *>(&v), sizeof(t));
		}
		Double(Name_t const &name, std::istream &is)
		: Tag(name)
		{
			is.read(reinterpret_cast<char *>(&v), sizeof(t));
		}
	};
	struct Tag::ByteArray : Tag
	{
		static ID_t const ID = 7;
		using t = std::vector<Byte::t>;
		t v;

		using Tag::Tag;

		ByteArray(t const &v)
		: v(v)
		{
		}
		ByteArray(t &&v)
		: v(v)
		{
		}
		ByteArray(Name_t const &name, t const &v)
		: Tag(name)
		, v(v)
		{
		}
		ByteArray(Name_t const &name, t &&v)
		: Tag(name)
		, v(v)
		{
		}

		virtual ID_t id() const
		{
			return ID;
		}
		virtual std::unique_ptr<Tag> clone() const
		{
			return std::unique_ptr<Tag>(new ByteArray(*this));
		}

		virtual std::ostream &writePayload(std::ostream &os) const
		{
			Int(static_cast<Int::t>(v.size())).writePayload(os);
			for(auto const &b : v)
			{
				os.write(reinterpret_cast<char const *>(&b), sizeof(b));
			}
			return os;
		}
		ByteArray(Name_t const &name, std::istream &is)
		: Tag(name)
		{
			t::size_type size = Int(L"", is).v;
			for(auto i = t::size_type(); i < size; ++i)
			{
				v.push_back(Byte(L"", is).v);
			}
		}
	};
	struct Tag::String : Tag
	{
		static ID_t const ID = 8;
		using t = std::wstring;
		t v;

		using Tag::Tag;

		String(t const &v)
		: v(v)
		{
		}
		String(t &&v)
		: v(v)
		{
		}
		String(Name_t const &name, t const &v)
		: Tag(name)
		, v(v)
		{
		}
		String(Name_t const &name, t &&v)
		: Tag(name)
		, v(v)
		{
		}

		virtual ID_t id() const
		{
			return ID;
		}
		virtual std::unique_ptr<Tag> clone() const
		{
			return std::unique_ptr<Tag>(new String(*this));
		}

		virtual std::ostream &writePayload(std::ostream &os) const
		{
			Short(static_cast<Short::t>(v.length())).writePayload(os);
			return os.write(reinterpret_cast<char const *>(v.data()), v.length()*sizeof(t::value_type));
		}
		String(Name_t const &name, std::istream &is)
		: Tag(name)
		{
			t::size_type size = Short(L"", is).v;
			std::unique_ptr<t::value_type[]> str {new t::value_type[size+1]};
			str[size] = L'\0';
			is.read(reinterpret_cast<char *>(&(str[0])), size*sizeof(t::value_type));
			v = str.get();
		}
	};
	struct Tag::List : Tag
	{
		static ID_t const ID = 9;
		ID_t of;
		using t = std::vector<std::unique_ptr<Tag>>;
		t v;

		List(List const &from)
		: Tag(from.name)
		, of(from.of)
		{
			v.reserve(from.v.size());
			for(auto const &tag : from.v)
			{
				v.push_back(tag->clone());
			}
		}
		List(List &&) = default;
		List &operator=(List const &from) = default;
		List &operator=(List &&) = default;

		List(ID_t type, std::initializer_list<t::value_type> init = {})
		: List(Name_t(), of, init)
		{
		}
		List(Name_t const &name, ID_t type, std::initializer_list<t::value_type> init = {})
		: Tag(name)
		, of(type)
		, v(init)
		{
		}

		virtual ID_t id() const
		{
			return ID;
		}
		virtual std::unique_ptr<Tag> clone() const
		{
			return std::unique_ptr<Tag>(new List(*this));
		}

		void purge()
		{
			for(auto it = v.begin(); it != v.end();)
			{
				if((*it)->id() != of)
				{
					it = v.erase(it);
				}
				else ++it;
			}
		}

		virtual std::ostream &writePayload(std::ostream &os) const
		{
			List temp {*this};
			temp.purge();
			Byte(of).writePayload(os);
			Int(static_cast<Int::t>(temp.v.size())).writePayload(os);
			for(auto const &tag : temp.v)
			{
				tag->writePayload(os);
			}
			return os;
		}
		List(Name_t const &name, ID_t type, std::istream &is)
		: Tag(name)
		, of(type)
		{
			t::size_type size = Int(L"", is).v;
			for(auto i = t::size_type(); i < size; ++i)
			{
				v.push_back(readers.at(of)(L"", is));
			}
		}
	};
	struct Tag::Compound : Tag
	{
		static ID_t const ID = 10;
		using t = std::map<std::reference_wrapper<Name_t const>, std::unique_ptr<Tag>>;
		t v;

		using Tag::Tag;

		Compound(std::initializer_list<t::mapped_type> init)
		: Compound(Name_t())
		{
		}
		Compound(Name_t const &name, std::initializer_list<t::mapped_type> init)
		: Tag(name)
		{
			for(auto &tag : init)
			{
				v.emplace(tag->name, tag);
			}
		}

		virtual ID_t id() const
		{
			return ID;
		}
		virtual std::unique_ptr<Tag> clone() const
		{
			return std::unique_ptr<Tag>(new Compound(*this));
		}

		virtual std::ostream &writePayload(std::ostream &os) const
		{
			for(auto &e : v)
			{
				e.second->write(os);
			}
			return End::END.write(os);
		}
		Compound(Name_t const &name, std::istream &is)
		: Tag(name)
		{
			while(is.peek() != End::ID)
			{
				auto tag = read(is);
				v.emplace(tag->name, tag);
			}
			is.ignore();
		}
	};
	struct Tag::IntArray : Tag
	{
		static ID_t const ID = 11;
		using t = std::vector<Int::t>;
		t v;

		using Tag::Tag;

		IntArray(t const &v)
		: v(v)
		{
		}
		IntArray(t &&v)
		: v(v)
		{
		}
		IntArray(Name_t const &name, t const &v)
		: Tag(name)
		, v(v)
		{
		}
		IntArray(Name_t const &name, t &&v)
		: Tag(name)
		, v(v)
		{
		}

		virtual ID_t id() const
		{
			return ID;
		}
		virtual std::unique_ptr<Tag> clone() const
		{
			return std::unique_ptr<Tag>(new IntArray(*this));
		}

		virtual std::ostream &writePayload(std::ostream &os) const
		{
			Int(static_cast<Int::t>(v.size())).writePayload(os);
			for(auto const &i : v)
			{
				os.write(reinterpret_cast<char const *>(&i), sizeof(i));
			}
			return os;
		}
		IntArray(Name_t const &name, std::istream &is)
		: Tag(name)
		{
			t::size_type size = Int(L"", is).v;
			for(auto i = t::size_type(); i < size; ++i)
			{
				v.push_back(Int(L"", is).v);
			}
		}
	};
	inline std::ostream &Tag::write(std::ostream &os) const
	{
		Byte(id()).writePayload(os);
		String(name).writePayload(os);
		return writePayload(os);
	}
	inline std::unique_ptr<Tag> Tag::read(std::istream &is)
	{
		ID_t type {Byte(L"", is).v};
		Name_t name {String(L"", is).v};
		return readers.at(type)(name, is);
	}
}

#endif
