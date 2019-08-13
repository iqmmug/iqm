package at.mug.iqm.commons.util.plot;

/*
 * #%L
 * Project: IQM - API
 * File: PermutationForTest.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2019 Helmut Ahammer, Philipp Kainz
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PermutationForTest

{

	static public void sortchar(char[] buffer, int len)

	{

		for (int i = 1; i < len; i++)

		{

			for (int j = 0; j < len - i; j++)

			{

				if (buffer[j] > buffer[j + 1])

				{

					char temp = buffer[j];

					buffer[j] = buffer[j + 1];

					buffer[j + 1] = temp;

				}

			}

		}

	}

	static public boolean NextPermuation(char[] p, int len)

	{

		for (int k = len - 1; k > 0; k--)

		{

			if (p[k - 1] >= p[k])

				continue;

			else

			{

				if (k <= len - 3)

				{

					char newchar = p[k - 1];

					int anchor = -1;

					for (int j = len - 1; j >= k; j--)

					{

						if (newchar < p[j])

						{

							anchor = j;

							break;

						}

					}

					if (anchor == -1)

						return false;

					char ch = p[k - 1];

					p[k - 1] = p[anchor];

					p[anchor] = ch;

					char[] tbuffer = new char[len - k];

					for (int m = 0; m < len - k; m++)

						tbuffer[m] = p[k + m];

					// sortchar(p+i,len - k);

					sortchar(tbuffer, len - k);

					for (int n = 0; n < len - k; n++)

						p[k + n] = tbuffer[n];

					return true;

				}

				else

				{

					char[] tempptr = new char[3];

					tempptr[0] = p[p.length - 3];

					tempptr[1] = p[p.length - 2];

					tempptr[2] = p[p.length - 1];

					int count = 3;

					for (int i = count - 1; i > 0; i--)

					{

						if (tempptr[i - 1] >= tempptr[i])

							continue;

						else

						{

							if (i <= count - 2)

							{

								if (tempptr[i + 1] > tempptr[i - 1])

								{

									char ch = tempptr[i + 1];

									tempptr[i + 1] = tempptr[i];

									tempptr[i] = tempptr[i - 1];

									tempptr[i - 1] = ch;

								}

								else

								{

									char ch = tempptr[i - 1];

									tempptr[i - 1] = tempptr[i];

									tempptr[i] = tempptr[i + 1];

									tempptr[i + 1] = ch;

								}

							}

							else

							{

								char ch = tempptr[i];

								tempptr[i] = tempptr[i - 1];

								tempptr[i - 1] = ch;

							}

							p[p.length - 3] = tempptr[0];

							p[p.length - 2] = tempptr[1];

							p[p.length - 1] = tempptr[2];

							return true;

						}

					}

					return false;

				}

			}

		}

		return false;

	}

	@SuppressWarnings("unused")
	public static void main(String args[]) throws Exception

	{

		String inpstring = "";

		InputStreamReader input = new InputStreamReader(System.in);

		BufferedReader reader = new BufferedReader(input);

		try

		{

			System.out.print("Enter a string to find permutation:");

			inpstring = reader.readLine();

			int len = inpstring.length();

			inpstring = inpstring.toUpperCase();

		}

		catch (Exception e)

		{

			e.printStackTrace();

		}

		char[] buffer = inpstring.toCharArray();

		// sortchar(buffer, buffer.length); // use it only if you require

		int count = 0;

		while (true)

		{

			System.out.println(buffer);

			count++;

			if (NextPermuation(buffer, buffer.length) == false)

				break;

		}

		System.out.println("\nCount: " + count);

	}

}
