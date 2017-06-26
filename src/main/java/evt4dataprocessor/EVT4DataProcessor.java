package evt4dataprocessor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;

public class EVT4DataProcessor {
	
	public ArrayList<EVT4ImageData> images = new ArrayList<EVT4ImageData>();
	short rxstatus;
	short numberOfReadouts;
	EVT4ProcessingStatus evt4ProcessingStatus = new EVT4ProcessingStatus();
	public int numberOfExcitations;
	ArrayList<Integer> correctImagesIndices = new ArrayList<Integer>();
	int lastProcessedCorrectimageIdx;
	ArrayList<Short> frame_counter_vect = new ArrayList<Short>();
	ArrayList<Float> u = new ArrayList<Float>();
	ArrayList<Float> image = new ArrayList<Float>();
	ArrayList<Float> meanImage = new ArrayList<Float>();
	public EVT4ChannelData evt4ChannelData;
	public EVT4ImageData evt4ImageData;
	public EVT4ExcitationData evt4ExcitationData;
	private Boolean rawData;
	private final Integer DIVISOR = 65536;
	
	public EVT4DataProcessor()
	{
		Reset();
	}
	
	public void Reset()
	{
		images.clear();
		correctImagesIndices.clear();
		numberOfExcitations = 0;
		numberOfReadouts = 0;
		lastProcessedCorrectimageIdx = -1;
		rawData = false;

		for (int i = 0; i < 8; ++i)
			evt4ProcessingStatus.image_idxs[i] = -1;

		evt4ProcessingStatus.exception = false;
		Clean();
	}
	
	public void Clean()
	{
		evt4ProcessingStatus.exception = false;
		evt4ProcessingStatus.readout_wordlen = 0;
		evt4ProcessingStatus.num_of_readouts = 0;
		evt4ProcessingStatus.samples_counter = 0;
		evt4ProcessingStatus.readout_data_counter = 0;
		evt4ProcessingStatus.excitation_counter = 0;

		for (int i = 0; i < 8; ++i)
			evt4ProcessingStatus.electrodes_config_prev_vect[i] = 0;

		evt4ProcessingStatus.electrodes_config = 0;
		evt4ProcessingStatus.readout_number = -1;

		for (int i = 0; i < 8; ++i)
			evt4ProcessingStatus.prev_word[i] = 0;

		evt4ProcessingStatus.state = State.STATE_OUT_OF_FRAME;

		for (int i = 0; i < 8; ++i)
			evt4ProcessingStatus.state_to_be_continued[i] = State.STATE_OUT_OF_FRAME;

		evt4ProcessingStatus.exidx = 0;
		for(int i = 0; i < 8; ++i)
			evt4ProcessingStatus.searchImageStart[i] = true;
	}

	public int CreateIndexOfCorrectImages()
	{
		int correctImagesNumber = 0;

		for (int i = 0; i < images.size(); ++i) {
			boolean correctImage = CheckImageCorrectness(i);

			if (correctImage) {
				correctImagesIndices.add(i);
				correctImagesNumber++;
				lastProcessedCorrectimageIdx = i;
			}
		}

		return correctImagesNumber;
	}
	
	boolean CheckImageCorrectness(int imageIdx)
	{
		if (imageIdx >= images.size())
			return false;

		boolean correctImage = true;

		if (images.get(imageIdx).excitations.size() == numberOfExcitations) {
			for (int j = 0; j < images.get(imageIdx).excitationData.size(); ++j) {
				if (images.get(imageIdx).excitationData.get(j).channelsData.size() == numberOfReadouts * 4) {
					for (int k = 0; k < images.get(imageIdx).excitationData.get(j).channelsData.size(); ++k) {
						if (images.get(imageIdx).excitationData.get(j).channelsData.get(k).numel != images.get(imageIdx).excitationData.get(j).channelsData.get(k).sampleData.size()
								|| images.get(imageIdx).excitationData.get(j).channelsData.get(k).numel <2 ) 
						{
							correctImage = false;
							break;
						}
					}

					if (!correctImage)
						break;
				} else {
					correctImage = false;
					break;
				}
			}
		} else
			correctImage = false;

		return correctImage;
	}
	
	public void Process(byte[] buf, long bufByteLen)
	{
		int v = 0, i = 0, idx = 0;
		int crc = 0, temp;
		short index;
		int data;
		int u16_ptr, w_ptr;
		byte u8_ptr = 0;
		ByteBuffer buff = ByteBuffer.wrap(buf);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		EVT4Header evt4Header = new EVT4Header();
		ReadoutHeader readoutHeader = new ReadoutHeader();
		
		boolean exists = false;
		
		while (idx < bufByteLen) 
		{
			switch (evt4ProcessingStatus.state)
			{
			case STATE_OUT_OF_FRAME:
				
				if(idx+19 >= bufByteLen)
				{
					idx += 19;
				}					
				else if((buff.getInt(idx)) == 877942341)
				{
					buff.position(idx);
					evt4Header.marker = buff.getInt();
					evt4Header.header_bytelen = buff.getShort();
					evt4Header.frame_bytelen = buff.getInt();
					evt4Header.jiffies = buff.getInt();
					evt4Header.crc = buff.getShort();
					evt4Header.rxstatus = buff.getShort();
					evt4Header.frame_counter = buff.getShort();
					//evt4Header.reserved = new short[buff.remaining()];
					//buff.asShortBuffer().get(evt4Header.reserved,0,evt4Header.reserved.length);
					buff.rewind();
					
					frame_counter_vect.add(evt4Header.frame_counter);
					v = evt4Header.rxstatus;

					for (evt4ProcessingStatus.num_of_readouts = 0; v != 0; evt4ProcessingStatus.num_of_readouts++)
						v &= v - 1;

					numberOfReadouts = evt4ProcessingStatus.num_of_readouts;

					//evt4header = evt4header;
					idx += evt4Header.header_bytelen;
					evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;
				}
				else 
				{
					idx += 1;
					evt4ProcessingStatus.state = State.STATE_OUT_OF_FRAME;
				}
				break;
				
			case STATE_EVT4_FRAME_READOUT_NUMBER:

				if (evt4ProcessingStatus.readout_number != -1) 
				{
					//we are after processing readout nr readout_number. We have to remember samples to concatenate to them in the next frame.
					evt4ProcessingStatus.electrodes_config_prev_vect[evt4ProcessingStatus.readout_number] = evt4ProcessingStatus.electrodes_config;
				}

				if (evt4ProcessingStatus.readout_number == evt4ProcessingStatus.num_of_readouts - 1 && idx < bufByteLen)  // to byl ostatni readout.
				{ 
					evt4ProcessingStatus.readout_number = -1;
					evt4ProcessingStatus.state = State.STATE_OUT_OF_FRAME;
				} else 
				{
					evt4ProcessingStatus.readout_number = buff.getShort(idx);
					idx += 2;
					evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_BYTELEN;
				}

				break;	
				
			case STATE_EVT4_FRAME_READOUT_BYTELEN:
				evt4ProcessingStatus.readout_wordlen = (buff.getShort(idx))/2;
				evt4ProcessingStatus.readout_data_counter = 2;

				if (evt4ProcessingStatus.readout_data_counter == evt4ProcessingStatus.readout_wordlen) 
				{
					evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;
				} 
				else 
				{
					if (evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] != State.STATE_OUT_OF_FRAME) 
					{
						evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number];
						evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_OUT_OF_FRAME;
						evt4ProcessingStatus.electrodes_config = evt4ProcessingStatus.electrodes_config_prev_vect[evt4ProcessingStatus.readout_number];
						if (images.size() > 0)
						{
							evt4ProcessingStatus.exidx = images.get(images.size() - 1).getIndexOfExcitation(evt4ProcessingStatus.electrodes_config);
						}
							
					} 
					else 
					{
						evt4ProcessingStatus.state = State.STATE_READOUT_FIND_FRAME_START;
					}
				}

				idx += 2;
				break;	
			
			
			case STATE_READOUT_FIND_FRAME_START:
				if ((buff.getShort(idx)) == -13142) 
				{
					evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_FRAME_START;
				} 
				else 
				{
					evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_FIND_FRAME_START;
	
					if (evt4ProcessingStatus.readout_data_counter++ >= evt4ProcessingStatus.readout_wordlen - 1)
						evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;
	
					idx += 2;
				}
				break;
				
			case STATE_READOUT_FRAME_START:
				
				if ((buff.getShort(idx)) == -13142) 
				{
					evt4ProcessingStatus.samples_counter = 0;

					evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_READ_CONFIG;

					if (evt4ProcessingStatus.readout_data_counter++ >= evt4ProcessingStatus.readout_wordlen - 1)
					{
						evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;
					}

					idx += 2;
				} else 
				{
					Clean();
					evt4ProcessingStatus.exception = true;
				}
				break;
				
			case STATE_READOUT_READ_CONFIG:
				if (idx + 4 <= bufByteLen && evt4ProcessingStatus.readout_data_counter + 2 <= evt4ProcessingStatus.readout_wordlen) 
				{
					evt4ProcessingStatus.electrodes_config = buff.getInt(idx);
					evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_PROCESS_CONFIG;
					idx += 4;
					evt4ProcessingStatus.readout_data_counter++;
				} 
				else 
				{
					evt4ProcessingStatus.prev_word[evt4ProcessingStatus.readout_number] = buff.getShort(idx);
					evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_RESUME_READ_CONFIG;
					idx += 2;

					if (evt4ProcessingStatus.readout_data_counter++ >= evt4ProcessingStatus.readout_wordlen - 1)
					{
						evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;
					}
				}

				break;
				
			case STATE_READOUT_RESUME_READ_CONFIG:
				short temp3 = buff.getShort(idx);
				short temp4 = (short) (temp3 << 16);
				evt4ProcessingStatus.electrodes_config = ((buff.getShort(idx) << 16) | evt4ProcessingStatus.prev_word[evt4ProcessingStatus.readout_number]);				
				evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_PROCESS_CONFIG;
				idx += 2;

				break;
				
			case STATE_READOUT_PROCESS_CONFIG:
				
				evt4ImageData = new EVT4ImageData();
				evt4ExcitationData = new EVT4ExcitationData();
				if (evt4ProcessingStatus.searchImageStart[evt4ProcessingStatus.readout_number] && evt4ProcessingStatus.electrodes_config != 0x1) 
				{
					evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_FIND_FRAME_START;
				} 
				else 
				{
					evt4ProcessingStatus.searchImageStart[evt4ProcessingStatus.readout_number] = false;
					if (evt4ProcessingStatus.image_idxs[0] == -1) 
					{				
						images.add(evt4ImageData);

						for (int j = 0; j < 8; ++j)
							evt4ProcessingStatus.image_idxs[j] = 0;
					}

					if (evt4ProcessingStatus.exception) {
						//if (images[0].excitations.size() > 0 && images[0].excitations[0] == evt4ProcessingStatus.electrodes_config && evt4ProcessingStatus.readout_number == 0) {
						if (evt4ProcessingStatus.electrodes_config == 0x1 && evt4ProcessingStatus.readout_number == 0) {
							images.add(evt4ImageData);

							for (int j = 0; j < 8; ++j)
								evt4ProcessingStatus.image_idxs[j] = images.size() - 1;

							evt4ProcessingStatus.exception = false;
						} else {
							evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_FIND_FRAME_START;

							if (evt4ProcessingStatus.readout_data_counter++ >= evt4ProcessingStatus.readout_wordlen - 1)
								evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;

							break;
						}
					}

					//czy w biezacym obrazie jest juz taka konfiguracja elektrod wymuszajacych?
					index = images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).getIndexOfExcitation(evt4ProcessingStatus.electrodes_config);

					if (index > -1) {
						//czy dla dla tej konfiguracji elektrod wymuszajacych sa juz dane dla aktualnego readoutu? Je�li tak, to zacznij kolejny obraz
						if (images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitationData.get(index).checkIfChannelsDataExists(evt4ProcessingStatus.readout_number)) 
						{

							if (images.size() - 1 == evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]) 
							{
								images.add(evt4ImageData);
							}

							evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number] = images.size() - 1;
							index = images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).getIndexOfExcitation(evt4ProcessingStatus.electrodes_config);

							if (index == -1) 
							{
								images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitationData.add(evt4ExcitationData);
								images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitations.add(evt4ProcessingStatus.electrodes_config);
								index = images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).getIndexOfExcitation(evt4ProcessingStatus.electrodes_config);
							}
						}
					} 
					else 
					{
						images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitationData.add(evt4ExcitationData);
						images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitations.add(evt4ProcessingStatus.electrodes_config);
						index = (short) (images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitations.size() - 1);
						evt4ProcessingStatus.exidx = images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitations.size() - 1;
					}
					
					evt4ProcessingStatus.exidx = index;
					images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitationData.get(index).setChannelsDataFlags(evt4ProcessingStatus.readout_number);

					while (images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitationData.get(index).channelsData.size() < 4 * evt4ProcessingStatus.readout_number + 4)
					{
						evt4ChannelData = new EVT4ChannelData();
						images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitationData.get(index).channelsData.add(evt4ChannelData);
					}

					evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_SAMPLES_START;
				}

				if (evt4ProcessingStatus.readout_data_counter++ >= evt4ProcessingStatus.readout_wordlen - 1)
				{
					evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;
				}

				break;
				
			case STATE_READOUT_RESUME_READ_CRC:
				evt4ProcessingStatus.crc = (((buff.getShort(idx)) << 16) | evt4ProcessingStatus.prev_word[evt4ProcessingStatus.readout_number]);
				evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_PROCESS_CRC;
				idx += 2;
				break;
				
			case STATE_READOUT_PROCESS_CRC:
				crc = evt4ProcessingStatus.crc;
				index = images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).getIndexOfExcitation(evt4ProcessingStatus.electrodes_config);
				temp = crc >> 8;

				for (int j = 0; j < 4; ++j)
				{
					images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitationData.get(index).channelsData.get(evt4ProcessingStatus.readout_number * 4 + j).numel = (short) temp;
				}
				evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_FRAME_END;

				if (evt4ProcessingStatus.readout_data_counter++ >= evt4ProcessingStatus.readout_wordlen - 1)
				{
					evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;
				}
				break;
				
			case STATE_READOUT_SAMPLES_START:
				readoutHeader.counter = buff.get(idx);
				readoutHeader.marker = buff.get(idx+1);

				if (readoutHeader.marker == -61) 
				{
					evt4ProcessingStatus.samples_counter++;
					evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_SAMPLE0;
					idx += 2;

					if (evt4ProcessingStatus.readout_data_counter++ >= evt4ProcessingStatus.readout_wordlen - 1)
					{
						evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;
					}
				} 
				else 
				{
					if (idx + 4 <= bufByteLen && evt4ProcessingStatus.readout_data_counter + 2 <= evt4ProcessingStatus.readout_wordlen) 
					{
						evt4ProcessingStatus.crc = buff.getInt(idx);
						idx += 4;
						evt4ProcessingStatus.readout_data_counter++;
						evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_PROCESS_CRC;
					} 
					else 
					{
						evt4ProcessingStatus.prev_word[evt4ProcessingStatus.readout_number] = buff.getShort(idx);
						evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_RESUME_READ_CRC;
						idx += 2;

						if (evt4ProcessingStatus.readout_data_counter++ >= evt4ProcessingStatus.readout_wordlen - 1)
						{
							evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;
						}
					}
				}

				break;
				
			case STATE_READOUT_SAMPLE0:
				evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_SAMPLE1;
								
				index = images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).getIndexOfExcitation(evt4ProcessingStatus.electrodes_config);	
				
				if(index == -1)
				{
					Clean();
					evt4ProcessingStatus.exception = true;
				}
				else
				{
					data = buff.getShort(idx) & 0xffff;
					images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitationData.get(index).channelsData.get(evt4ProcessingStatus.readout_number * 4 + 0).sampleData.add((short)((short)(data << 8) | (short)(data >> 8)));

					if (evt4ProcessingStatus.readout_data_counter++ >= evt4ProcessingStatus.readout_wordlen - 1)
					{
						evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;
					}

					idx += 2;
				}				
				break;
				
			case STATE_READOUT_SAMPLE1:
				evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_SAMPLE2;
				index = images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).getIndexOfExcitation(evt4ProcessingStatus.electrodes_config);
				if(index == -1)
				{
					Clean();
					evt4ProcessingStatus.exception = true;
				}
				else
				{
					data = buff.getShort(idx) & 0xffff;
					images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitationData.get(index).channelsData.get(evt4ProcessingStatus.readout_number * 4 + 1).sampleData.add((short)((short)(data << 8) | (short)(data >> 8)));

					if (evt4ProcessingStatus.readout_data_counter++ >= evt4ProcessingStatus.readout_wordlen - 1)
					{
						evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;
					}

					idx += 2;
				}				
				break;
				
			case STATE_READOUT_SAMPLE2:
				evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_SAMPLE3;
				index = images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).getIndexOfExcitation(evt4ProcessingStatus.electrodes_config);
				if(index == -1)
				{
					Clean();
					evt4ProcessingStatus.exception = true;
				}
				else
				{
					data = buff.getShort(idx) & 0xffff;
					images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitationData.get(index).channelsData.get(evt4ProcessingStatus.readout_number * 4 + 2).sampleData.add((short)((short)(data << 8) | (short)(data >> 8)));

					if (evt4ProcessingStatus.readout_data_counter++ >= evt4ProcessingStatus.readout_wordlen - 1)
					{
						evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;
					}

					idx += 2;
				}
				break;
				
			case STATE_READOUT_SAMPLE3:
				evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_SAMPLES_START;
				index = images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).getIndexOfExcitation(evt4ProcessingStatus.electrodes_config);
				if(index == -1)
				{
					Clean();
					evt4ProcessingStatus.exception = true;
				}
				else
				{
					data = buff.getShort(idx) & 0xffff;
					images.get(evt4ProcessingStatus.image_idxs[evt4ProcessingStatus.readout_number]).excitationData.get(index).channelsData.get(evt4ProcessingStatus.readout_number * 4 + 3).sampleData.add((short)((short)(data << 8) | (short)(data >> 8)));

					if (evt4ProcessingStatus.readout_data_counter++ >= evt4ProcessingStatus.readout_wordlen - 1)
					{
						evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;
					}

					idx += 2;
				}
				break;
				
			case STATE_READOUT_FRAME_END:

				if ((buff.getShort(idx) == -26986)) 
				{
					evt4ProcessingStatus.state = evt4ProcessingStatus.state_to_be_continued[evt4ProcessingStatus.readout_number] = State.STATE_READOUT_FRAME_START;

					if (evt4ProcessingStatus.readout_data_counter++ >= evt4ProcessingStatus.readout_wordlen - 1)
					{
						evt4ProcessingStatus.state = State.STATE_EVT4_FRAME_READOUT_NUMBER;
					}

					idx += 2;
				} 
				else 
				{
					Clean();
					evt4ProcessingStatus.exception = true;
				}

				break;
				
			}		
		}		
	}
	
	public void SetUVector(int imageIndex, int electrodeConfigIndex) 
	{
		u.clear();
		EVT4ExcitationData evt4ExcitationData = images.get(imageIndex).excitationData.get(electrodeConfigIndex);
		int excitation = images.get(imageIndex).excitations.get(electrodeConfigIndex);
		ArrayList<Integer> excitation_electrodes_numbers = new ArrayList<Integer>();

		for (int i = 0; i < 32; ++i) {
			if ((excitation & 1) !=0)
				excitation_electrodes_numbers.add(1);
			else
				excitation_electrodes_numbers.add(0);

			excitation >>= 1;
		}

		for (int i = 0; i < evt4ExcitationData.channelsData.size(); ++i) 
		{
			if (excitation_electrodes_numbers.get(i) == 0) 
			{
			    if(rawData) {
			        int length = evt4ExcitationData.channelsData.get(i).numel;
	                int one_third_length = length / 3;
	                double log2_length = Math.pow(2, (Math.log10(one_third_length)/Math.log10(2)));
	                double reminder = (one_third_length - log2_length) / 2;
	                double a = reminder;
	                double b = a + log2_length;
	                double c = b + reminder + reminder;
	                double d = c + log2_length;
	                double e = d + reminder + reminder;
	                double f = e + log2_length;
	                float sumA = 0, sumB = 0, sumC = 0;

	                for (int j = 0; j < length; ++j) 
	                {
	                    if (j >= a && j <= b) {
	                        sumA -= evt4ExcitationData.channelsData.get(i).sampleData.get(j);
	                    } else if (j >= c && j <= d) {
	                        sumB -= evt4ExcitationData.channelsData.get(i).sampleData.get(j);
	                    } else if (j >= e && j <= f) {
	                        sumC -= evt4ExcitationData.channelsData.get(i).sampleData.get(j);
	                    }
	                }

	                u.add((float) (sumB / log2_length - ((sumC + sumA) / (2 * log2_length))));
			    } else {
			        Short fraction_short = evt4ExcitationData.channelsData.get(i).sampleData.get(1);
	                float fraction = (float)fraction_short/(float)DIVISOR;
	                u.add((float)(evt4ExcitationData.channelsData.get(i).sampleData.get(0) + fraction));
			    }
			}
		}

		int middle = 0;

		for (int i = 0; i < excitation_electrodes_numbers.size(); ++i) {
			if (excitation_electrodes_numbers.get(i) == 1) 
			{
				middle = i;
				break;
			}
		}
		Collections.rotate(u, -middle);
	}
	
	public ArrayList<Float> GetUVector()
	{
		return u;
	}
	
	public void SetImageVector(int imageIndex) 
	{
		image.clear();

		if (correctImagesIndices.contains(imageIndex)) 
		{
			for (int i = 0; i < images.get(imageIndex).excitations.size(); ++i) {
				SetUVector(imageIndex, i);
				image.addAll(u);
			}
		}
	}
	
	public void LoadDataFromFile(String path, Boolean isRawData)
	{
		try
		{
		File f = new File(path);
		int bufByteLen = (int) f.length();
		InputStream is = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(is); 
		byte[] buffer = new byte[bufByteLen];			
		dis.readFully(buffer);
		if(isRawData) rawData = true;
		else rawData = false;
		Process(buffer, bufByteLen);
		dis.close();
		CreateIndexOfCorrectImages();
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
	}
	
	public ArrayList<Float> GetImageVector()
	{
		return image;
	}
	
	public void setNumberOfExcitations(int num)
	{
		numberOfExcitations = num;
	}
}
