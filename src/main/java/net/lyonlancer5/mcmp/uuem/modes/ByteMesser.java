package net.lyonlancer5.mcmp.uuem.modes;

import java.security.SecureRandom;
import java.util.Random;

/**
 * A collection of methods to mess around with byte arrays
 * 
 * @author Lyonlancer5
 */
public enum ByteMesser {

	/**
	 * Inverts each byte in the array
	 */
	NEGATE(new MesserImpl() {

		@Override
		public byte[] doMess(Random rand, byte[] src) {
			if (src == null) {
				byte[] b = new byte[1024];
				rand.nextBytes(b);
				return b;
			}

			for (int x = 0; x < src.length; x++) {
				byte b = src[x];

				if (b != Byte.MIN_VALUE) {
					src[x] *= -1;
				}
			}
			return src;
		}

	}),

	/**
	 * Adds random data in between the source array's indices
	 */
	GENERATE_RANDOM(new MesserImpl() {

		@Override
		public byte[] doMess(Random rand, byte[] src) {
			if (src == null) {
				byte[] b = new byte[1024];
				rand.nextBytes(b);
				return b;
			}

			rand.setSeed(0xFACADE);

			byte[] buf = new byte[src.length];
			rand.nextBytes(buf);

			byte[] dst = new byte[src.length * 2];

			for (int x = 0, dx = 0; x < dst.length; x += 2) {
				dst[x] = src[dx];
				dst[x + 1] = buf[dx];
				dx++;
			}

			return dst;
		}

	}),

	REMOVE_RANDOM(new MesserImpl() {

		@Override
		public byte[] doMess(Random rand, byte[] src) {
			if (src == null) {
				byte[] b = new byte[1024];
				rand.nextBytes(b);
				return b;
			}

			if (src.length % 2 != 0)
				throw new IllegalArgumentException("Array length must be even");
			byte[] dst = new byte[src.length / 2];
			int dx = 0;
			for (int x = 0; x < src.length; x += 2) {
				dst[dx] = src[x];
				dx++;
			}

			return dst;
		}

	}),

	SHIFT_OFFSET(new MesserImpl() {

		@Override
		public byte[] doMess(Random rand, byte[] src) {
			if (src == null) {
				byte[] b = new byte[1024];
				rand.nextBytes(b);
				return b;
			}
			rand.setSeed(0xFACADE);

			int shift = rand.nextInt(src.length - 1);

			byte[] dst = new byte[src.length + 4];
			if (shift == 0)
				shift++;

			dst[0] = (byte) ((shift >>> 24) & 0xFF);
			dst[1] = (byte) ((shift >>> 16) & 0xFF);
			dst[2] = (byte) ((shift >>> 8) & 0xFF);
			dst[3] = (byte) ((shift >>> 0) & 0xFF);

			int trg;

			for (int x = 0; x < src.length; x++) {
				trg = shift + x;
				if (trg >= src.length)
					trg -= src.length;

				byte b = src[trg];
				dst[x + 4] = b;
			}

			return dst;
		}

	}),

	/**
	 * Shift the indices of the byte array. The inverse method of
	 * {@link ByteMesser#SHIFT_OFFSET}
	 */
	SHIFT_REVERSE(new MesserImpl() {

		@Override
		public byte[] doMess(Random rand, byte[] src) {
			if (src == null) {
				byte[] b = new byte[1024];
				rand.nextBytes(b);
				return b;
			}
			if (src.length < 4)
				throw new IllegalArgumentException("Invalid byte array input: missing 4 byte shift index");

			int shift = (src[0] << 24) + (src[1] << 16) + (src[2] << 8) + (src[3] << 0);

			byte[] buf = new byte[src.length - 4];
			System.arraycopy(src, 4, buf, 0, buf.length);

			byte[] dst = new byte[buf.length];
			int trg;

			for (int x = buf.length - 1; x >= 0; x--) {
				trg = x - shift;
				if (trg < 0)
					trg += buf.length;

				byte b = buf[trg];
				dst[x] = b;
			}

			return dst;
		}

	});

	private final SecureRandom rand;
	private final MesserImpl theImpl;

	private ByteMesser(MesserImpl implIn) {
		rand = new SecureRandom();
		theImpl = implIn;
	}

	/**
	 * Generate a new array from the given data
	 * 
	 * @param src
	 *            The source byte array
	 */
	public byte[] generate(byte[] src) {
		return theImpl.doMess(rand, src);
	}

	private interface MesserImpl {
		byte[] doMess(Random rand, byte[] src);
	}
}
