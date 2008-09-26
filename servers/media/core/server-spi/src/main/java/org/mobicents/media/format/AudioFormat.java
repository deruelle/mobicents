package org.mobicents.media.format;

import org.mobicents.media.Format;
import org.mobicents.media.FormatUtils;

/**
 * Standard JMF class -- see <a href="http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/format/AudioFormat.html"
 * target="_blank">this class in the JMF Javadoc</a>. Coding complete.
 * 
 * @author Ken Larson
 * 
 */
public class AudioFormat extends Format {

	public static final int BIG_ENDIAN = 1;
	public static final int LITTLE_ENDIAN = 0;
	public static final int SIGNED = 1;
	public static final int UNSIGNED = 0;
	protected double sampleRate = NOT_SPECIFIED;
	protected int sampleSizeInBits = NOT_SPECIFIED;
	protected int channels = NOT_SPECIFIED;
	protected int endian = NOT_SPECIFIED;
	protected int signed = NOT_SPECIFIED;
	protected double frameRate = NOT_SPECIFIED;
	protected int frameSizeInBits = NOT_SPECIFIED;
	public static final String LINEAR = "LINEAR";
	public static final String ULAW = "ULAW";
	public static final String ULAW_RTP = "ULAW/rtp";
	public static final String ALAW = "alaw"; // strange that this is lower
	// case and ULAW is not...
	public static final String SPEEX = "speex";
	public static final String IMA4 = "ima4";
	public static final String IMA4_MS = "ima4/ms";
	public static final String MSADPCM = "msadpcm";
	public static final String DVI = "dvi";
	public static final String G723 = "g723";
	public static final String G728 = "g728";
	public static final String G729 = "g729";
	public static final String G729A = "g729a";
	public static final String GSM = "gsm";
	public static final String GSM_MS = "gsm/ms";
	public static final String MAC3 = "MAC3";
	public static final String MAC6 = "MAC6";
	public static final String TRUESPEECH = "truespeech";
	public static final String MSNAUDIO = "msnaudio";
	public static final String MPEGLAYER3 = "mpeglayer3";
	public static final String VOXWAREAC8 = "voxwareac8";
	public static final String VOXWAREAC10 = "voxwareac10";
	public static final String VOXWAREAC16 = "voxwareac16";
	public static final String VOXWAREAC20 = "voxwareac20";
	public static final String VOXWAREMETAVOICE = "voxwaremetavoice";
	public static final String VOXWAREMETASOUND = "voxwaremetasound";
	public static final String VOXWARERT29H = "voxwarert29h";
	public static final String VOXWAREVR12 = "voxwarevr12";
	public static final String VOXWAREVR18 = "voxwarevr18";
	public static final String VOXWARETQ40 = "voxwaretq40";
	public static final String VOXWARETQ60 = "voxwaretq60";
	public static final String MSRT24 = "msrt24";
	public static final String MPEG = "mpegaudio";
	public static final String DOLBYAC3 = "dolbyac3"; // TODO: these are not
	// used in this impl,
	// only added for
	// serialization
	// compatibility.
	// TODO: probably used for computeDuration, to make it faster.
	double multiplier = -1.0;
	int margin = 0;
	boolean init = false;

	public AudioFormat(String encoding) {
		super(encoding);
		super.dataType = super.byteArray;
	}

	public AudioFormat(String encoding, double sampleRate, int sampleSizeInBits, int channels) {
		super(encoding);
		this.dataType = byteArray;
		this.sampleRate = sampleRate;
		this.sampleSizeInBits = sampleSizeInBits;
		this.channels = channels;
	}

	public AudioFormat(String encoding, double sampleRate, int sampleSizeInBits, int channels, int endian, int signed) {
		super(encoding);
		this.dataType = byteArray;
		this.sampleRate = sampleRate;
		this.sampleSizeInBits = sampleSizeInBits;
		this.channels = channels;
		this.endian = endian;
		this.signed = signed;

	}

	public AudioFormat(String encoding, double sampleRate, int sampleSizeInBits, int channels, int endian, int signed, int frameSizeInBits, double frameRate, Class dataType) {
		super(encoding, dataType);
		this.sampleRate = sampleRate;
		this.sampleSizeInBits = sampleSizeInBits;
		this.channels = channels;
		this.endian = endian;
		this.signed = signed;
		this.frameSizeInBits = frameSizeInBits;
		this.frameRate = frameRate;
	}

	public double getSampleRate() {
		return sampleRate;
	}

	public int getSampleSizeInBits() {
		return sampleSizeInBits;
	}

	public int getChannels() {
		return channels;
	}

	public int getEndian() {
		return endian;
	}

	public int getSigned() {
		return signed;
	}

	public int getFrameSizeInBits() {
		return frameSizeInBits;
	}

	public double getFrameRate() {
		return frameRate;
	}

	public long computeDuration(long length) {
		// TODO: this calculation does not always give the exact same result as
		// the reference implementation.
		// see JavaSound API for information on frames and samples.
		//http://java.sun.com/j2se/1.4.2/docs/api/javax/sound/sampled/AudioFormat
		// .html

		final double frameRate;
		final int frameSizeInBits;

		if (FormatUtils.specified(this.frameRate) && FormatUtils.specified(this.frameSizeInBits)) {
			frameRate = this.frameRate;
			frameSizeInBits = this.frameSizeInBits;
		} else if (FormatUtils.specified(this.sampleRate) && FormatUtils.specified(this.sampleSizeInBits) && FormatUtils.specified(this.channels)) {
			// this calculation is correct for uncompressed data. TODO: are
			// there any formats where this is not correct?
			frameRate = this.sampleRate;
			frameSizeInBits = this.sampleSizeInBits * this.channels;
		} else {
			return -1L;
		}
		return 1000L * (long) (length * 8 * 1000000.0 / ((double) frameRate * (double) frameSizeInBits));
	}

	/**
	 * Overrides default toString. Flag indicates if output should be equal to
	 * parsed string: dolbyac3, 2, 1, 2;
	 * 
	 * @param asInput
	 *            <ul>
	 *            <li> <b>true</b> - will return in the exact same way it has been fed: dolbyac3, 2, 1, 2 - this format is defined in rtp media format
	 *            <li> <b>false</b> - will return in format like: dolbyac3, 2.0 Hz, 1-bit, Stereo, Unsigned, 6.0 frame rate
	 *            </ul>
	 * @return
	 */
	public String toString(boolean asInput) {
		// examples:
		// dolbyac3, Unknown Sample Rate
		// dolbyac3, 2.0 Hz, 1-bit, Stereo, Unsigned, 6.0 frame rate,
		// FrameSize=5 bits
		// dolbyac3, 2.0 Hz, 1-bit, 0-channel, Unsigned, 6.0 frame rate,
		// FrameSize=5 bits
		// dolbyac3, 2.0 Hz, 1-bit, 3-channel, Unsigned, 6.0 frame rate,
		// FrameSize=5 bits

		// TODO: use "KHz" when appropriate.

		final StringBuffer b = new StringBuffer();
		b.append(encoding);

		// b.append(", ");
		if (FormatUtils.specified(sampleRate)) {
			// FIXME: This removes .0
			b.append(", " + (int) sampleRate);
			if (!asInput)
				b.append(" Hz");
		} else {
			if (!asInput)
				b.append(", Unknown Sample Rate");
		}
		if (FormatUtils.specified(sampleSizeInBits)) {
			b.append(", ");
			b.append("" + sampleSizeInBits);
			if (!asInput)
				b.append("-bit");
		}
		if (FormatUtils.specified(channels)) {
			b.append(", ");
			if (!asInput) {
				if (channels == 1) {
					b.append("Mono");
				} else if (channels == 2) {
					b.append("Stereo");
				} else {
					b.append("" + channels + "-channel");
				}
			} else {

				b.append("" + channels);
			}
		}

		if (FormatUtils.specified(endian) && FormatUtils.specified(sampleSizeInBits) && sampleSizeInBits > 8) {

			b.append(", ");
			if (!asInput) {
				if (endian == BIG_ENDIAN) {

					b.append("BigEndian");
				} else if (endian == LITTLE_ENDIAN) {

					b.append("LittleEndian");
				} else { // unknown, don't append anything
				}
			} else {
				b.append("" + endian);
			}
		}

		if (FormatUtils.specified(signed)) {
			b.append(", ");
			if (!asInput) {
				if (signed != SIGNED) {
					b.append("Unsigned");
				} else {
					b.append("Signed");
				}
			} else {
				b.append("" + signed);
			}
		}

		if (FormatUtils.specified(frameRate)) {
			b.append(", ");
			if (!asInput) {
				b.append("" + frameRate + " frame rate");
			} else {
				b.append("" + frameRate);
			}

		}
		if (FormatUtils.specified(frameSizeInBits)) {
			if (!asInput) {
				b.append(", FrameSize=" + frameSizeInBits + " bits");
			} else {
				b.append(", " + frameSizeInBits);
			}
		}
		return b.toString();
	}

	@Override
	public String toString() {

		return toString(true);
	}

	@Override
	public boolean equals(Object format) {
		if (!super.equals(format)) {
			return false;
		}
		if (!(format instanceof AudioFormat)) {
			return false;
		}

		final AudioFormat oCast = (AudioFormat) format;
		return this.sampleRate == oCast.sampleRate && this.sampleSizeInBits == oCast.sampleSizeInBits && this.channels == oCast.channels && this.endian == oCast.endian
				&& this.signed == oCast.signed && this.frameSizeInBits == oCast.frameSizeInBits && this.frameRate == oCast.frameRate;
	}

	@Override
	public boolean matches(Format format) {
		if (!super.matches(format)) {
			// if (getClass() == FormatUtils.audioFormatClass) {
			// FormatTraceUtils.traceMatches(this, format, false); // otherwise
			// let subclass trace
			// }
			return false;
		}

		if (!(format instanceof AudioFormat)) {
			final boolean result = true;
			// if (getClass() == FormatUtils.audioFormatClass){
			// FormatTraceUtils.traceMatches(this, format, result);
			// }
			return result;
		}

		final AudioFormat oCast = (AudioFormat) format;

		final boolean result = FormatUtils.matches(this.sampleRate, oCast.sampleRate) && FormatUtils.matches(this.sampleSizeInBits, oCast.sampleSizeInBits)
				&& FormatUtils.matches(this.channels, oCast.channels) && FormatUtils.matches(this.endian, oCast.endian) && FormatUtils.matches(this.signed, oCast.signed)
				&& FormatUtils.matches(this.frameSizeInBits, oCast.frameSizeInBits) && FormatUtils.matches(this.frameRate, oCast.frameRate);

		// if (getClass() == FormatUtils.audioFormatClass){
		// FormatTraceUtils.traceMatches(this, format, result); // otherwise let
		// subclass trace
		// }

		return result;

	}

	@Override
	public Format intersects(Format other) {

		final Format result = super.intersects(other);

		if (other instanceof AudioFormat) {
			final AudioFormat resultCast = (AudioFormat) result;

			final AudioFormat oCast = (AudioFormat) other;
			if (getClass().isAssignableFrom(other.getClass())) {
				// "other" was cloned.

				if (FormatUtils.specified(this.sampleRate)) {
					resultCast.sampleRate = this.sampleRate;
				}
				if (FormatUtils.specified(this.sampleSizeInBits)) {
					resultCast.sampleSizeInBits = this.sampleSizeInBits;
				}
				if (FormatUtils.specified(this.channels)) {
					resultCast.channels = this.channels;
				}
				if (FormatUtils.specified(this.endian)) {
					resultCast.endian = this.endian;
				}
				if (FormatUtils.specified(this.signed)) {
					resultCast.signed = this.signed;
				}
				if (FormatUtils.specified(this.frameSizeInBits)) {
					resultCast.frameSizeInBits = this.frameSizeInBits;
				}
				if (FormatUtils.specified(this.frameRate)) {
					resultCast.frameRate = this.frameRate;
				}
			} else if (other.getClass().isAssignableFrom(getClass())) { // this
				// was
				// cloned

				if (!FormatUtils.specified(resultCast.sampleRate)) {
					resultCast.sampleRate = oCast.sampleRate;
				}
				if (!FormatUtils.specified(resultCast.sampleSizeInBits)) {
					resultCast.sampleSizeInBits = oCast.sampleSizeInBits;
				}
				if (!FormatUtils.specified(resultCast.channels)) {
					resultCast.channels = oCast.channels;
				}
				if (!FormatUtils.specified(resultCast.endian)) {
					resultCast.endian = oCast.endian;
				}
				if (!FormatUtils.specified(resultCast.signed)) {
					resultCast.signed = oCast.signed;
				}
				if (!FormatUtils.specified(resultCast.frameSizeInBits)) {
					resultCast.frameSizeInBits = oCast.frameSizeInBits;
				}
				if (!FormatUtils.specified(resultCast.frameRate)) {
					resultCast.frameRate = oCast.frameRate;
				}
			}
		}

		// if (getClass() == FormatUtils.audioFormatClass) {
		// FormatTraceUtils.traceIntersects(this, other, result);
		// }

		return result;
	}

	@Override
	public Object clone() {
		return new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, endian, signed, frameSizeInBits, frameRate, dataType);

	}

	@Override
	protected void copy(Format f) {
		super.copy(f);
		final AudioFormat oCast = (AudioFormat) f; // it has to be a
		// AudioFormat, or
		// ClassCastException will
		// be thrown.
		this.sampleRate = oCast.sampleRate;
		this.sampleSizeInBits = oCast.sampleSizeInBits;
		this.channels = oCast.channels;
		this.endian = oCast.endian;
		this.signed = oCast.signed;
		this.frameSizeInBits = oCast.frameSizeInBits;
		this.frameRate = oCast.frameRate;

	}
}
