#ifndef MinecraftNamedBinaryTagClasses_HeaderPlusPlus
#define MinecraftNamedBinaryTagClasses_HeaderPlusPlus
#include <string>
#include <cstdint>
#include <cstddef>
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

		using Name_t = std::string;
		Name_t const name;
		using ID_t = std::int8_t;

		Tag() = default;
		Tag(Name_t const &name)
		: name(name)
		{
		}
		Tag(Tag const &) = default;
		Tag(Tag &&) = default;
		Tag &operator=(Tag const &) = delete;
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
		static ID_t const ID;

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
		static ID_t const ID;
		using t = std::int8_t;
		t v;

		//using Tag::Tag;
		Byte() = default;
		Byte(Name_t const &name)
		: Tag(name)
		{
		}
		Byte(Byte const &) = default;
		Byte(Byte &&) = default;

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
		static ID_t const ID;
		using t = std::int16_t;
		t v;

		//using Tag::Tag;
		Short() = default;
		Short(Name_t const &name)
		: Tag(name)
		{
		}
		Short(Short const &) = default;
		Short(Short &&) = default;

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
			for(auto start = reinterpret_cast<char const *>(&v), it = start+sizeof(t)-1; it != start-1; --it)
			{
				os.write(it, 1);
			}
			return os;
		}
		Short(Name_t const &name, std::istream &is)
		: Tag(name), v()
		{
			char arr[sizeof(t)];
			is.read(arr, sizeof(t));
			for(auto it = arr+sizeof(t)-1, start = it; it != arr-1; --it)
			{
				v += (*it) << (8*(start-it));
			}
		}
	};
	struct Tag::Int : Tag
	{
		static ID_t const ID;
		using t = std::int32_t;
		t v;

		//using Tag::Tag;
		Int() = default;
		Int(Name_t const &name)
		: Tag(name)
		{
		}
		Int(Int const &) = default;
		Int(Int &&) = default;

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
			for(auto start = reinterpret_cast<char const *>(&v), it = start+sizeof(t)-1; it != start-1; --it)
			{
				os.write(it, 1);
			}
			return os;
		}
		Int(Name_t const &name, std::istream &is)
		: Tag(name), v()
		{
			char arr[sizeof(t)];
			is.read(arr, sizeof(t));
			for(auto it = arr+sizeof(t)-1, start = it; it != arr-1; --it)
			{
				v += (*it) << (8*(start-it));
			}
		}
	};
	struct Tag::Long : Tag
	{
		static ID_t const ID;
		using t = std::int64_t;
		t v;

		//using Tag::Tag;
		Long() = default;
		Long(Name_t const &name)
		: Tag(name)
		{
		}
		Long(Long const &) = default;
		Long(Long &&) = default;

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
			for(auto start = reinterpret_cast<char const *>(&v), it = start+sizeof(t)-1; it != start-1; --it)
			{
				os.write(it, 1);
			}
			return os;
		}
		Long(Name_t const &name, std::istream &is)
		: Tag(name), v()
		{
			char arr[sizeof(t)];
			is.read(arr, sizeof(t));
			for(auto it = arr+sizeof(t)-1, start = it; it != arr-1; --it)
			{
				v += (*it) << (8*(start-it));
			}
		}
	};
	struct Tag::Float : Tag
	{
		static ID_t const ID;
		using t = float;
		t v;

		//using Tag::Tag;
		Float() = default;
		Float(Name_t const &name)
		: Tag(name)
		{
		}
		Float(Float const &) = default;
		Float(Float &&) = default;

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
			for(auto start = reinterpret_cast<char const *>(&v), it = start+sizeof(t)-1; it != start-1; --it)
			{
				os.write(it, 1);
			}
			return os;
		}
		Float(Name_t const &name, std::istream &is)
		: Tag(name)
		{
			auto start = reinterpret_cast<char *>(&v);
			is.read(start, sizeof(t));
			for(std::ptrdiff_t i = 0; i < sizeof(t)/2; ++i)
			{
				std::swap(*(start+i), *(start+sizeof(t)-i-1));
			}
		}
	};
	struct Tag::Double : Tag
	{
		static ID_t const ID;
		using t = double;
		t v;

		//using Tag::Tag;
		Double() = default;
		Double(Name_t const &name)
		: Tag(name)
		{
		}
		Double(Double const &) = default;
		Double(Double &&) = default;

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
			for(auto start = reinterpret_cast<char const *>(&v), it = start+sizeof(t)-1; it != start-1; --it)
			{
				os.write(it, 1);
			}
			return os;
		}
		Double(Name_t const &name, std::istream &is)
		: Tag(name)
		{
			auto start = reinterpret_cast<char *>(&v);
			is.read(start, sizeof(t));
			for(std::ptrdiff_t i = 0; i < sizeof(t)/2; ++i)
			{
				std::swap(*(start+i), *(start+sizeof(t)-i-1));
			}
		}
	};
	struct Tag::ByteArray : Tag
	{
		static ID_t const ID;
		using t = std::vector<Byte::t>;
		t v;

		//using Tag::Tag;
		ByteArray() = default;
		ByteArray(Name_t const &name)
		: Tag(name)
		{
		}
		ByteArray(ByteArray const &) = default;
		ByteArray(ByteArray &&) = default;

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
			t::size_type size = Int(u8"", is).v;
			for(auto i = t::size_type(); i < size; ++i)
			{
				v.push_back(Byte(u8"", is).v);
			}
		}
	};
	struct Tag::String : Tag
	{
		static ID_t const ID;
		using t = std::string;
		t v;

		//using Tag::Tag;
		String() = default;
		String(Name_t const &name)
		: Tag(name)
		{
		}
		String(String const &) = default;
		String(String &&) = default;

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
			t::size_type size = Short(u8"", is).v;
			std::unique_ptr<t::value_type[]> str {new t::value_type[size+1]};
			str[size] = '\0';
			is.read(reinterpret_cast<char *>(&(str[0])), size*sizeof(t::value_type));
			v = str.get();
		}
	};
	struct Tag::List : Tag
	{
		static ID_t const ID;
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

		List(ID_t type, std::initializer_list<t::value_type::pointer> init = {})
		: List(Name_t(), of, init)
		{
		}
		List(Name_t const &name, ID_t type, std::initializer_list<t::value_type::pointer> init = {})
		: Tag(name)
		, of(type)
		{
			v.reserve(init.size());
			for(auto tag : init)
			{
				v.push_back(t::value_type(tag));
			}
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
			t::size_type size = Int(u8"", is).v;
			for(auto i = t::size_type(); i < size; ++i)
			{
				v.push_back(readers.at(of)(u8"", is));
			}
		}
	};
	struct Tag::Compound : Tag
	{
		static ID_t const ID;
	private:
		template<typename T>
		struct refwrapcomp
		{
			bool operator()(std::reference_wrapper<T> const &a, std::reference_wrapper<T> const &b) const
			{
				return a.get() < b.get();
			}
		};
	public:
		using t = std::map<std::reference_wrapper<Name_t const>, std::unique_ptr<Tag>, refwrapcomp<Name_t const>>;
		t v;

		Compound() = default;
		Compound(Name_t const &name)
		: Tag(name)
		{
		}
		Compound(Compound const &from)
		{
			for(auto const &elem : from.v)
			{
				auto tag = elem.second->clone();
				v[tag->name] = std::move(tag);
			}
		}
		Compound(Compound &&) = default;

		Compound(std::initializer_list<t::mapped_type::pointer> init)
		: Compound(Name_t(), init)
		{
		}
		Compound(Name_t const &name, std::initializer_list<t::mapped_type::pointer> init)
		: Tag(name)
		{
			for(auto tag : init)
			{
				v[tag->name] = t::mapped_type(tag);
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
				v[tag->name] = std::move(tag);
			}
			is.ignore();
		}
	};
	struct Tag::IntArray : Tag
	{
		static ID_t const ID;
		using t = std::vector<Int::t>;
		t v;

		//using Tag::Tag;
		IntArray() = default;
		IntArray(Name_t const &name)
		: Tag(name)
		{
		}
		IntArray(IntArray const &) = default;
		IntArray(IntArray &&) = default;

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
			t::size_type size = Int(u8"", is).v;
			for(auto i = t::size_type(); i < size; ++i)
			{
				v.push_back(Int(u8"", is).v);
			}
		}
	};
	inline std::ostream &Tag::write(std::ostream &os) const
	{
		Byte(id()).writePayload(os);
		String(u8"", name).writePayload(os);
		return writePayload(os);
	}
	inline std::unique_ptr<Tag> Tag::read(std::istream &is)
	{
		ID_t type {Byte(u8"", is).v};
		Name_t name {String(u8"", is).v};
		return readers.at(type)(name, is);
	}
}

#endif
