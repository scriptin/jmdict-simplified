import { Readable } from 'stream';

import {
  parseMetadata,
  parseEntries,
  put,
  updatePathAfterValue,
  Metadata,
} from './parser';
// @ts-ignore
import makeParser from 'stream-json';

describe('put', () => {
  it('throws an error when path is empty', () => {
    expect(() => put({}, [], 'something')).toThrow(/empty path/i);
  });

  it('puts a value for an immediate key', () => {
    const obj = {};
    put(obj, ['foo'], 'bar');
    expect(obj).toHaveProperty('foo', 'bar');
  });

  it('unable to put a value for a nested key if immediate parent is not  initialized', () => {
    const obj = {}; // doesn't have a 'foo' child yet
    expect(() => put(obj, ['foo', 'nested'], 'bar')).toThrow(/undefined/i);
  });

  it('puts a value for a nested key', () => {
    const obj = { foo: {} };
    put(obj, ['foo', 'nested'], 'bar');
    expect(obj).toHaveProperty('foo.nested', 'bar');
  });

  it('puts an item in an empty array', () => {
    const obj = { foo: [] };
    put(obj, ['foo', 0], 'bar');
    expect(obj.foo).toContain('bar');
  });

  it('appends an item in a non-empty array', () => {
    const obj = { foo: [1, 2, 3] };
    put(obj, ['foo', 3], 'bar');
    expect(obj.foo).toContain('bar');
    expect(obj.foo[3]).toEqual('bar');
  });

  it('does not insert or overwrite items at random indexes', () => {
    const obj = { foo: [1, 2, 3] };
    expect(() => put(obj, ['foo', 1], 'bar')).toThrow(/invalid path/i);
  });

  it('add properties on existing array items', () => {
    const obj = { foo: [{ nested1: 1 }] };
    put(obj, ['foo', 0, 'nested2'], 2);
    expect(obj.foo[0]).toHaveProperty('nested2', 2);
  });

  it('throws an error when trying to add properties on non-existing array items', () => {
    const obj = { foo: [{ nested1: 1 }] };
    expect(() => put(obj, ['foo', 999, 'nested2'], 2)).toThrow(/undefined/i);
  });
});

describe('updatePathAfterValue', () => {
  it('pops the last item if it is a string (key of an object)', () => {
    const path = [1, 2, 3, 'foo'];
    updatePathAfterValue(path);
    expect(path).toHaveLength(3);
    expect(path).not.toContain('foo');
  });

  it('increments the last items if it is an integer (index of an array)', () => {
    const path = [1, 2, 3];
    updatePathAfterValue(path);
    expect(path).toHaveLength(3);
    expect(path[2]).toEqual(4);
  });
});

describe('parseMetadata', () => {
  it.each(['words', 'characters'])(
    'parses an object until it sees the "%s" key and returns the parsed part',
    (entryField) => {
      const expectedParsed = {
        foo: 'bar',
        pi: 3.14,
        someArray: [1, 2, 3],
        objects: [{ a: 1 }, { b: 2 }, { c: 3 }],
        keysAndValues: {
          a: true,
          b: false,
          c: null,
          d: {},
          e: [],
          f: 3.14,
          g: '',
        },
      };
      const ignored = {
        ignored1: {},
        ignored2: [],
        ignored3: '',
        ignored4: 0,
        ignored5: true,
        ignored6: false,
        ignored7: null,
      };
      const obj = {
        ...expectedParsed,
        [entryField]: [],
        ...ignored,
      };
      const serializedJson = JSON.stringify(obj);
      const s = new Readable();
      const parser = s.pipe(makeParser({ packValues: true }));
      s.push(serializedJson);
      s.push(null);
      const handler = jest.fn();
      parseMetadata(parser, handler);
      parser.on('end', () => {
        expect(handler).toBeCalledWith(expect.objectContaining(expectedParsed));
        expect(handler).not.toBeCalledWith(
          expect.objectContaining({ words: [] }),
        );
        expect(handler).not.toBeCalledWith(expect.objectContaining(ignored));
      });
    },
  );
});

describe('parseWords', () => {
  it('parses an array of arbitrary objects', () => {
    const obj1 = {};
    const obj2 = { a: 1, b: '', c: true, d: false, e: null, f: {}, g: [] };
    const obj3 = {
      foo: ['bar', 'baz'],
    };
    const obj4 = {
      foo: {
        bar: 1,
        baz: 2,
      },
    };
    const arr = [obj1, obj2, obj3, obj4];
    const serializedJson = JSON.stringify(arr);
    const s = new Readable();
    const parser = s.pipe(makeParser({ packValues: true }));
    s.push(serializedJson);
    s.push(null);
    const handler = jest.fn();
    const metadata = {} as Metadata;
    parseEntries(parser, metadata, handler);
    parser.on('end', () => {
      expect(handler).toBeCalledTimes(4);
      expect(handler).nthCalledWith(1, expect.objectContaining(obj1), metadata);
      expect(handler).nthCalledWith(2, expect.objectContaining(obj2), metadata);
      expect(handler).nthCalledWith(3, expect.objectContaining(obj3), metadata);
      expect(handler).nthCalledWith(4, expect.objectContaining(obj4), metadata);
    });
  });
});
