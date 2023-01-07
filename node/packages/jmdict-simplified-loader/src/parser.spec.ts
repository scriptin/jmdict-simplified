import { put } from './parser';

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
